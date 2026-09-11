# EV1 · Evidencia end-to-end (IDaaS real)

Fecha: 2026-09-10 · IDaaS: Microsoft Entra ID, tenant DuocUC (`72fd0b5a-…`) · Resource Server: SkillLink API
(`SecurityConfig`, perfil `local`/`prod`, `JWT_ISSUER` y `JWT_AUDIENCE` reales) · Tokens obtenidos por
Authorization Code + PKCE (Azure CLI pre-autorizada en la API) y observados con sus claims, nunca completos.

## Tokens utilizados

| Token | `aud` | `scp` | `roles` | Cómo se obtuvo |
|---|---|---|---|---|
| A · lectura sin rol | `393a4087-…` | `skilllink.read` | — | rol `ADMIN` retirado y CLI pre-autorizada solo para `skilllink.read` |
| B · escritura sin rol | `393a4087-…` | `skilllink.read skilllink.write` | — | rol `ADMIN` retirado |
| C · escritura + ADMIN | `393a4087-…` | `skilllink.read skilllink.write` | `["ADMIN"]` | usuario con App Role asignado |

`iss` observado en todos: `https://login.microsoftonline.com/72fd0b5a-8a6a-4cff-89f6-bde961f7e250/v2.0` (`ver: 2.0`).

## Matriz observada

| # | Escenario | Request | Token | Esperado | Observado | Qué demuestra |
|---|---|---|---|---:|---:|---|
| 1 | Público | `GET /api/v1/public/health` | — | 200 | **200** | recurso sin autenticación |
| 2 | Catálogo público | `GET /api/v1/catalogo/publicaciones` | — | 200 | **200** | lectura pública persistida |
| 3 | Protegido sin token | `GET /api/v1/auth/me` | — | 401 | **401** (`WWW-Authenticate: Bearer`) | la API exige autenticación |
| 4 | Token inválido | `GET /api/v1/auth/me` | `abc.def.ghi` | 401 | **401** | firma no verificable |
| 5 | Token válido | `GET /api/v1/auth/me` | A | 200 | **200** · `SCOPE_skilllink.read` | firma, `iss`, `aud`, `exp` aceptados |
| 6 | Onboarding | `GET /api/v1/usuarios/me` | C | 200 | **200** · persona creada (`creadoEnEstaSesion: true`) | identidad resuelta contra el dominio |
| 7 | Escritura sin scope | `POST /api/v1/auth/write-check` | A | 403 | **403** (problem+json) | autorización por scope |
| 8 | Publicar sin scope | `POST /api/v1/catalogo/publicaciones` | A | 403 | **403** | dominio protegido por scope |
| 9 | Publicar con scope | `POST /api/v1/catalogo/publicaciones` | C | 201 | **201** (id 14, visible luego en el catálogo público) | escritura autorizada |
| 10 | Borrar propia | `DELETE /api/v1/catalogo/publicaciones/14` | C | 204 | **204** | propiedad del recurso |
| 11 | Borrar ajena | `DELETE /api/v1/catalogo/publicaciones/1` | B | 403 | **403** | propiedad del recurso (email del token ≠ dueño) |
| 12 | Admin sin rol | `GET /api/v1/admin/ping` | B | 403 | **403** | autorización por App Role |
| 13 | Admin de dominio sin rol | `GET /api/v1/admin/suscripciones` | B | 403 | **403** | recurso administrativo real |
| 14 | Admin con rol | `GET /api/v1/admin/suscripciones` | C | 200 | **200** | `ROLE_ADMIN` desde claim `roles` |
| 15 | Catálogo maestro sin rol | `POST /api/v1/categorias-servicio` | B | 403 | **403** | escritura maestra solo ADMIN |
| 16 | Catálogo maestro con rol | `POST /api/v1/categorias-servicio` | C | 201 | **201** | |
| 17 | CORS origen permitido | `GET /api/v1/public/health` + `Origin: http://localhost:4200` | — | 200 + `Access-Control-Allow-Origin` | **200** + cabecera presente | CORS explícito |
| 18 | CORS origen no permitido | `GET /api/v1/public/health` + `Origin: http://malicioso.example` | — | 403 | **403** | origen no autorizado |

Nota de diseño: un token con `roles: ["ADMIN"]` también puede escribir (los administradores moderan
publicaciones), por eso el escenario 7/8 se demuestra con un token **sin** rol.

## Mismos escenarios a través de AWS API Gateway

Con la sesión del laboratorio activa se observó por `https://5ztfy0tn42.execute-api.us-east-1.amazonaws.com`:
`/api/v1/public/health` → 200, `/api/v1/auth/me` → 401 (problem+json), preflight `OPTIONS` con
`Access-Control-Allow-Origin` solo para el frontend publicado y `localhost:4200`, origen desconocido → 403.
El API Gateway no altera el `Authorization: Bearer`, por lo que la matriz anterior es idéntica pasando por él.

## Frontend (Angular + MSAL)

Ruta `/seguridad` de la SPA: reproduce los escenarios 1, 2, 3, 4, 5, 7, 12, 13 con un clic y muestra
`iss`, `aud`, `scp`, `roles`, `iat`, `exp` del Access Token sin exponer el token. Para provocar el 403 por scope
desde la SPA basta cambiar `apiScope` a `.../skilllink.read` en `config.json`; para el 403 por rol, usar un
usuario sin el App Role `ADMIN`.

## Automatización

`./mvnw test` ejecuta `SeguridadMatrizTests` (misma matriz con tokens simulados, sin red).

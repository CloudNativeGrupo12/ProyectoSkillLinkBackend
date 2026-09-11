# EV1 · Mapa de la evaluación aplicado a SkillLink

Índice de lo que exige `DSY1107 / evaluaciones/ev1` y dónde queda resuelto en este proyecto.
Fuente: `evaluaciones/ev1/proyecto/*.md`, `starters/*`, `referencias/*`.

## 1. Arquitectura obligatoria (01-arquitectura-y-alcance)

```text
Usuario ─► Angular SPA ─(Authorization Code + PKCE)─► Microsoft Entra ID (IDaaS)
                │  Access Token JWT (Bearer)
                ▼
        AWS API Gateway (HTTP API, CORS, routing)
                │
                ▼
        Spring Boot 4 · Resource Server (EC2)  ─►  PostgreSQL (RDS)
```

| Requisito | Dónde |
|---|---|
| Frontend SPA Angular | `SkillLinkFrontend/` (Angular 22, standalone, signals) |
| Backend Java + Spring Boot separado | `ProyectoSkillLinkBackend/SkillLink/` (Java 21, Spring Boot 4.1) |
| Comunicación HTTP/REST real | `core/api/*-api.real.ts` → `/api/v1/**` |
| Persistencia real | JPA + PostgreSQL (`dev`/`prod`), H2 (`local`/`test`), seed en `config/DataSeeder.java` |
| ≥ 2 entidades relacionadas | `Persona`–`Trabajador`–`Perfil`–`Publicacion`, `Servicio`–`CategoriaServicio` (N:M), geografía |

## 2. Frontend (02-frontend)

| Requisito | Dónde |
|---|---|
| Vista pública / inicio | `/`, `/categorias`, `/servicios` |
| Vista con datos del backend | `/servicios` (publicaciones), `/categorias`, home (profesionales destacados) |
| Flujo que modifica información | `/publicar-servicio` (POST), eliminar publicación en `/mi-perfil` (DELETE), suscripción Premium (POST público) |
| Zona protegible | `/publicar-servicio`, `/mi-perfil` con `authGuard` (MSAL) |
| Estados: éxito / error / 404 / 401 / 403 | `core/http/api-error.ts` + mensajes en páginas; página `/seguridad` muestra la matriz completa |
| Configuración separada del código | `public/config.json` cargado en runtime (`apiBaseUrl`, `clientId`, `tenantId`, `apiScope`) |

## 3. Backend y datos (03-backend-y-datos)

| Requisito | Endpoint |
|---|---|
| Operación pública | `GET /api/v1/public/health`, `GET /api/v1/catalogo/**`, `POST /api/v1/public/suscripciones` |
| Lectura protegible (autenticado) | `GET /api/v1/auth/me`, `GET /api/v1/usuarios/me`, `GET /api/v1/catalogo/perfiles/por-trabajador/{id}` |
| Modificación protegible (scope) | `POST /api/v1/catalogo/publicaciones`, `DELETE /api/v1/catalogo/publicaciones/{id}`, CRUD `/api/v1/publicaciones` |
| Operación administrativa (rol) | `GET /api/v1/admin/ping`, `GET /api/v1/admin/suscripciones`, escritura de catálogos maestros |
| Códigos HTTP coherentes | 200/201/204/400/401/403/404/409 (`exceptions/GlobalExceptionHandler.java`, RFC 7807) |
| Capas | `controller → services → repositories → entities` (solo DTOs hacia fuera) |
| CORS controlado | `config/CorsConfig.java` (`CORS_ALLOWED_ORIGINS`) + CORS del API Gateway |

## 4. Seguridad, identidad y autorización (04)

| Requisito | Dónde |
|---|---|
| OAuth 2.0 / OIDC con IDaaS | Microsoft Entra ID; SPA con `@azure/msal-browser` (`core/auth/auth.service.ts`) |
| Authorization Code + PKCE | `loginRedirect` / `acquireTokenSilent` (MSAL gestiona PKCE) |
| ID Token ≠ Access Token | La SPA envía solo el Access Token (`auth.interceptor.ts`); `/seguridad` muestra `aud`/`scp` |
| 2 niveles de acceso | Scope delegado `skilllink.write` (SCOPE_) y App Role `ADMIN` (ROLE_) |
| 401 / 403 / 2xx | `config/SecurityConfig.java`, `RestAuthenticationEntryPoint`, `RestAccessDeniedHandler` |
| Validación firma/iss/aud/exp | `SecurityConfig.jwtDecoder` + `AudienceValidator` |
| Sin secretos versionados | Todo por variables de entorno / `config.json`; `.gitignore` |

## 5. API Manager y cloud (05)

| Requisito | Dónde |
|---|---|
| `API_BASE_URL` configurable | `config.json → apiBaseUrl` (local `http://localhost:8080` o API Gateway) |
| Gateway con routing/CORS | `deploy/aws/` + `docs/AWS-DESPLIEGUE.md` (HTTP API `skilllink-api` → EC2:8080) |
| Backend conserva su seguridad | Spring Security valida el JWT aunque pase por el gateway |

## 6. Demostrabilidad (06) — matriz de evidencia

| Escenario | Request | Esperado | Cómo provocarlo |
|---|---|---:|---|
| Público | `GET /api/v1/public/health` | 200 | `/seguridad` → "Recurso público" o `docs/requests.http` |
| Protegido sin token | `GET /api/v1/auth/me` | 401 | `/seguridad` sin sesión |
| Token inválido | `GET /api/v1/auth/me` + `Bearer basura` | 401 | `/seguridad` → "token inválido" |
| Token válido | `GET /api/v1/auth/me` | 200 | `/seguridad` con sesión |
| Sin scope | `POST /api/v1/auth/write-check` (token solo `skilllink.read`) | 403 | cambiar `apiScope` en `config.json` a `.../skilllink.read` |
| Con scope | `POST /api/v1/catalogo/publicaciones` | 201 | `/publicar-servicio` |
| Sin rol | `GET /api/v1/admin/ping` | 403 | usuario sin App Role |
| Con rol ADMIN | `GET /api/v1/admin/ping` | 200 | usuario con App Role `ADMIN` asignado |

Los mismos escenarios están automatizados en `src/test/java/.../SeguridadMatrizTests.java` (`./mvnw test`).

## 7. Checklist (06) — estado

- [x] Frontend SPA Angular · [x] Backend Spring Boot separado · [x] HTTP/REST · [x] Persistencia · [x] ≥ 2 entidades
- [x] Consultar colección · [x] Ver recurso · [x] Operación que modifica · [x] Niveles de acceso con sentido de dominio
- [x] Pública · [x] Lectura protegible · [x] Modificación protegible · [x] HTTP coherente · [x] CORS controlado
- [x] OAuth2/OIDC · [x] PKCE · [x] 2 scopes/roles · [x] Spring Security · [x] Configurable · [x] Gateway interponible · [x] Sin secretos

Tenant configurado y matriz validada con tokens reales: `docs/AZURE-ENTRA-ID.md`, `docs/EVIDENCIA-E2E.md`.

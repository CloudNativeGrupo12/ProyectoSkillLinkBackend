# Probar SkillLink desde otra máquina

Todo lo necesario está versionado en dos repositorios (rama `feature/ev1-seguridad-cloud` hasta que se haga merge a `main`); no se requiere Docker ni credenciales secretas
para la prueba local (los identificadores del tenant de Entra ID son públicos).

```text
https://github.com/CloudNativeGrupo12/ProyectoSkillLinkBackend   (API Spring Boot)
https://github.com/CloudNativeGrupo12/SkillLinkFrontend          (SPA Angular)
```

## 1. Requisitos

| Herramienta | Versión | Nota |
|---|---|---|
| Java (JDK) | 21 | `java -version` |
| Maven | no hace falta | el repo incluye `mvnw` / `mvnw.cmd` |
| Node.js | 24 LTS (≥ 22.22) | `node --version` |
| Navegador | cualquiera | inicia sesión con una cuenta del tenant DuocUC (`@duocuc.cl`) |

## 2. Backend (terminal 1)

```bash
git clone -b feature/ev1-seguridad-cloud https://github.com/CloudNativeGrupo12/ProyectoSkillLinkBackend.git
cd ProyectoSkillLinkBackend/SkillLink
./run-local.sh            # Linux/macOS   (Windows: .\run-local.ps1)
```

Arranca en `http://localhost:8080` con H2 en archivo (`./data/`), carga datos de demostración y valida
Access Tokens reales de Entra ID (`JWT_ISSUER`/`JWT_AUDIENCE` ya definidos en el script).

Comprobación rápida:

```bash
curl -i http://localhost:8080/api/v1/public/health      # 200
curl -i http://localhost:8080/api/v1/auth/me            # 401
```

Pruebas automatizadas de la matriz 200/401/403: `./mvnw test`.

## 3. Frontend (terminal 2)

```bash
git clone -b feature/ev1-seguridad-cloud https://github.com/CloudNativeGrupo12/SkillLinkFrontend.git
cd SkillLinkFrontend
npm ci
npm start                 # http://localhost:4200
```

`public/config.json` ya apunta a `http://localhost:8080` y al App Registration `SkillLink-SPA`
(`http://localhost:4200` está registrado como redirect URI).

## 4. Recorrido de demostración

1. `http://localhost:4200` → **Iniciar sesión** (Microsoft, Authorization Code + PKCE) → vuelve autenticado.
2. **Seguridad** → *Ejecutar todos*: 200 público, 401 sin token, 401 token inválido, 200 con token,
   200/403 en escritura y administración según el usuario. *Inspeccionar* muestra `iss`, `aud`, `scp`, `roles`, `exp`.
3. **Publicar Servicio** → crea una publicación (201) que aparece en el catálogo público; en **Mi Perfil**
   se puede eliminar (204). Con `apiScope` = `.../skilllink.read` en `config.json` el mismo flujo da **403**.
4. Usuario con App Role `ADMIN` → `GET /api/v1/admin/ping` = 200; sin el rol = 403.
   El rol se asigna en Entra ID: *Enterprise applications → SkillLink-API → Users and groups*.

`docs/requests.http` permite repetir la matriz desde VS Code REST Client / IntelliJ con un Access Token.

## 5. Entorno cloud (AWS)

Ver `docs/AWS-DESPLIEGUE.md`. El laboratorio AWS Academy detiene los recursos al cerrar la sesión; con la
sesión activa: frontend `https://bds00rs4cd.execute-api.us-east-1.amazonaws.com`, API
`https://5ztfy0tn42.execute-api.us-east-1.amazonaws.com` (API Gateway con CORS → EC2 → RDS).

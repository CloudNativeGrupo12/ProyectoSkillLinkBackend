# Configuración de Microsoft Entra ID para SkillLink

Diseño (sigue `referencias/ENTRA-CONFIGURACION.md` adaptado al dominio):

```text
SkillLink-API      App Registration del Resource Server (expone scopes y App Role)
SkillLink-SPA      App Registration de la SPA Angular (cliente público, sin client_secret)
```

## 1. API (`SkillLink-API`)

1. **Expose an API** → Application ID URI: `api://<API_CLIENT_ID>`.
2. Scopes delegados (Admins and users):
   - `skilllink.read`  – Leer catálogo y perfil propio.
   - `skilllink.write` – Publicar y administrar servicios propios.
3. **App roles** → `Admin` (value `ADMIN`, Users/Groups).
4. **Manifest** → `requestedAccessTokenVersion: 2` (tokens v2: `iss = https://login.microsoftonline.com/<TENANT_ID>/v2.0`, `aud = <API_CLIENT_ID>`).

## 2. SPA (`SkillLink-SPA`)

1. **Authentication → Single-page application** con redirect URIs exactas:
   - `http://localhost:4200`
   - `https://<frontend-api-id>.execute-api.us-east-1.amazonaws.com` (frontend publicado)
2. **API permissions → My APIs → SkillLink-API** → `skilllink.read`, `skilllink.write` → *Grant admin consent*.
3. Sin `client_secret`.

## 3. Usuarios de prueba

- Usuario normal (sin rol): `GET /api/v1/admin/ping → 403`.
- Usuario admin: **Enterprise applications → SkillLink-API → Users and groups** → asignar rol `Admin` → `200`.

## 4. Valores a configurar (no son secretos)

| Dónde | Variable | Valor |
|---|---|---|
| Backend | `JWT_ISSUER` | `https://login.microsoftonline.com/<TENANT_ID>/v2.0` |
| Backend | `JWT_AUDIENCE` | `<API_CLIENT_ID>,api://<API_CLIENT_ID>` |
| Backend | `APP_WRITE_SCOPE` | `skilllink.write` |
| Backend | `APP_ADMIN_ROLE` | `ADMIN` |
| Frontend `config.json` | `auth.clientId` | `<SPA_CLIENT_ID>` |
| Frontend `config.json` | `auth.tenantId` | `<TENANT_ID>` |
| Frontend `config.json` | `auth.apiScope` | `api://<API_CLIENT_ID>/skilllink.write` |

Para demostrar el 403 por scope basta cambiar `apiScope` a `.../skilllink.read` y volver a iniciar sesión.

## 5. Valores reales del tenant (configuración pública, sin secretos)

Registrados el 2026-09-10 con `deploy/azure/crear-app-registrations.sh` en el tenant DuocUC:

```text
TENANT_ID=72fd0b5a-8a6a-4cff-89f6-bde961f7e250
API_CLIENT_ID=393a4087-9e4d-41c4-a0ce-1755b1ba0d2f
API_APPLICATION_ID_URI=api://393a4087-9e4d-41c4-a0ce-1755b1ba0d2f
SPA_CLIENT_ID=f459e214-84c8-4ada-bfa0-3855d6ddda74
JWT_ISSUER=https://login.microsoftonline.com/72fd0b5a-8a6a-4cff-89f6-bde961f7e250/v2.0
JWT_AUDIENCE=393a4087-9e4d-41c4-a0ce-1755b1ba0d2f,api://393a4087-9e4d-41c4-a0ce-1755b1ba0d2f
READ_SCOPE=api://393a4087-9e4d-41c4-a0ce-1755b1ba0d2f/skilllink.read
WRITE_SCOPE=api://393a4087-9e4d-41c4-a0ce-1755b1ba0d2f/skilllink.write
ADMIN_ROLE=ADMIN (app role id 7f2eea01-6ae9-4707-8119-9934377d17ef)
REDIRECT_URIS=http://localhost:4200, https://bds00rs4cd.execute-api.us-east-1.amazonaws.com
```

Observado en el Access Token emitido (v2): `iss` = issuer anterior, `aud` = `393a4087-…` (GUID, no `api://`),
`scp` = `skilllink.read skilllink.write`, `roles` = `["ADMIN"]` para el usuario con el rol asignado.

## 6. Automatización

`deploy/azure/crear-app-registrations.sh` crea ambos registros con Azure CLI (`az login` previo) e imprime los valores anteriores.

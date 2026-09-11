#!/usr/bin/env bash
# Crea los App Registrations de SkillLink en Microsoft Entra ID con Azure CLI.
# Requiere: az login (usuario con permiso para registrar aplicaciones) y python3.
# Uso: FRONT_URL=https://xxxx.execute-api.us-east-1.amazonaws.com ./crear-app-registrations.sh
set -euo pipefail

API_NAME="${API_NAME:-SkillLink-API}"
SPA_NAME="${SPA_NAME:-SkillLink-SPA}"
FRONT_URL="${FRONT_URL:-}"
READ_SCOPE_ID="$(python3 -c 'import uuid;print(uuid.uuid4())')"
WRITE_SCOPE_ID="$(python3 -c 'import uuid;print(uuid.uuid4())')"
ADMIN_ROLE_ID="$(python3 -c 'import uuid;print(uuid.uuid4())')"

TENANT_ID="$(az account show --query tenantId -o tsv)"
echo "Tenant: $TENANT_ID"

# ---------- API (Resource Server)
API_APP_ID="$(az ad app list --display-name "$API_NAME" --query '[0].appId' -o tsv)"
if [ -z "$API_APP_ID" ]; then
  API_APP_ID="$(az ad app create --display-name "$API_NAME" --sign-in-audience AzureADMyOrg --query appId -o tsv)"
  echo "Creada API: $API_APP_ID"
fi
API_OBJ_ID="$(az ad app show --id "$API_APP_ID" --query id -o tsv)"

# Application ID URI, scopes delegados, app role y tokens v2
cat > /tmp/skilllink-api-patch.json <<JSON
{
  "identifierUris": ["api://$API_APP_ID"],
  "api": {
    "requestedAccessTokenVersion": 2,
    "oauth2PermissionScopes": [
      {
        "id": "$READ_SCOPE_ID",
        "value": "skilllink.read",
        "type": "User",
        "isEnabled": true,
        "adminConsentDisplayName": "Leer catalogo y perfil",
        "adminConsentDescription": "Permite leer el catalogo de SkillLink y el perfil del usuario.",
        "userConsentDisplayName": "Leer tu perfil en SkillLink",
        "userConsentDescription": "Permite leer tu perfil y el catalogo."
      },
      {
        "id": "$WRITE_SCOPE_ID",
        "value": "skilllink.write",
        "type": "User",
        "isEnabled": true,
        "adminConsentDisplayName": "Publicar servicios",
        "adminConsentDescription": "Permite crear y administrar publicaciones propias en SkillLink.",
        "userConsentDisplayName": "Publicar servicios en tu nombre",
        "userConsentDescription": "Permite publicar y administrar tus servicios."
      }
    ]
  },
  "appRoles": [
    {
      "id": "$ADMIN_ROLE_ID",
      "value": "ADMIN",
      "displayName": "Admin",
      "description": "Administrador de SkillLink (catalogos maestros y moderacion).",
      "allowedMemberTypes": ["User"],
      "isEnabled": true
    }
  ]
}
JSON
# Si ya existian scopes/roles, Graph exige mantener sus ids: se conservan los existentes.
EXISTING="$(az rest --method GET --url "https://graph.microsoft.com/v1.0/applications/$API_OBJ_ID?\$select=api,appRoles" -o json)"
if [ "$(echo "$EXISTING" | python3 -c 'import sys,json;d=json.load(sys.stdin);print(len(d["api"]["oauth2PermissionScopes"])+len(d["appRoles"]))')" = "0" ]; then
  az rest --method PATCH --url "https://graph.microsoft.com/v1.0/applications/$API_OBJ_ID" --headers "Content-Type=application/json" --body @/tmp/skilllink-api-patch.json
  echo "API configurada: scopes skilllink.read / skilllink.write, app role ADMIN, tokens v2"
else
  echo "API ya tenia scopes/roles; se conservan."
  READ_SCOPE_ID="$(echo "$EXISTING" | python3 -c 'import sys,json;d=json.load(sys.stdin);print([s["id"] for s in d["api"]["oauth2PermissionScopes"] if s["value"]=="skilllink.read"][0])')"
  WRITE_SCOPE_ID="$(echo "$EXISTING" | python3 -c 'import sys,json;d=json.load(sys.stdin);print([s["id"] for s in d["api"]["oauth2PermissionScopes"] if s["value"]=="skilllink.write"][0])')"
fi
# Service principal de la API (necesario para asignar roles y consentir)
az ad sp show --id "$API_APP_ID" >/dev/null 2>&1 || az ad sp create --id "$API_APP_ID" >/dev/null

# ---------- SPA (cliente publico)
SPA_APP_ID="$(az ad app list --display-name "$SPA_NAME" --query '[0].appId' -o tsv)"
if [ -z "$SPA_APP_ID" ]; then
  SPA_APP_ID="$(az ad app create --display-name "$SPA_NAME" --sign-in-audience AzureADMyOrg --query appId -o tsv)"
  echo "Creada SPA: $SPA_APP_ID"
fi
SPA_OBJ_ID="$(az ad app show --id "$SPA_APP_ID" --query id -o tsv)"
REDIRECTS='"http://localhost:4200"'
[ -n "$FRONT_URL" ] && REDIRECTS="$REDIRECTS, \"$FRONT_URL\""
cat > /tmp/skilllink-spa-patch.json <<JSON
{
  "spa": { "redirectUris": [ $REDIRECTS ] },
  "web": { "redirectUris": [] },
  "requiredResourceAccess": [
    {
      "resourceAppId": "$API_APP_ID",
      "resourceAccess": [
        { "id": "$READ_SCOPE_ID", "type": "Scope" },
        { "id": "$WRITE_SCOPE_ID", "type": "Scope" }
      ]
    },
    {
      "resourceAppId": "00000003-0000-0000-c000-000000000000",
      "resourceAccess": [
        { "id": "e1fe6dd8-ba31-4d61-89e7-88639da4683d", "type": "Scope" },
        { "id": "37f7f235-527c-4136-accd-4a02d197296e", "type": "Scope" },
        { "id": "14dad69e-099b-42c9-810b-d002981feec1", "type": "Scope" }
      ]
    }
  ]
}
JSON
az rest --method PATCH --url "https://graph.microsoft.com/v1.0/applications/$SPA_OBJ_ID" --headers "Content-Type=application/json" --body @/tmp/skilllink-spa-patch.json
az ad sp show --id "$SPA_APP_ID" >/dev/null 2>&1 || az ad sp create --id "$SPA_APP_ID" >/dev/null
# Autorizar la SPA en la API (evita la pantalla de consentimiento) y consentimiento admin
az rest --method PATCH --url "https://graph.microsoft.com/v1.0/applications/$API_OBJ_ID" --headers "Content-Type=application/json" \
  --body "{\"api\":{\"preAuthorizedApplications\":[{\"appId\":\"$SPA_APP_ID\",\"delegatedPermissionIds\":[\"$READ_SCOPE_ID\",\"$WRITE_SCOPE_ID\"]}]}}"
az ad app permission admin-consent --id "$SPA_APP_ID" 2>/dev/null || echo "(admin-consent requiere rol de administrador; se puede otorgar desde el portal)"

cat <<TXT

==================== VALORES (no son secretos) ====================
TENANT_ID=$TENANT_ID
API_CLIENT_ID=$API_APP_ID
API_APPLICATION_ID_URI=api://$API_APP_ID
SPA_CLIENT_ID=$SPA_APP_ID
JWT_ISSUER=https://login.microsoftonline.com/$TENANT_ID/v2.0
JWT_AUDIENCE=$API_APP_ID,api://$API_APP_ID
READ_SCOPE=api://$API_APP_ID/skilllink.read
WRITE_SCOPE=api://$API_APP_ID/skilllink.write
ADMIN_ROLE=ADMIN
REDIRECT_URIS=$REDIRECTS

Asignar el rol ADMIN a un usuario:
  SP_ID=\$(az ad sp show --id $API_APP_ID --query id -o tsv)
  USER_ID=\$(az ad user show --id <correo> --query id -o tsv)
  az rest --method POST --url https://graph.microsoft.com/v1.0/users/\$USER_ID/appRoleAssignments \\
    --body "{\"principalId\":\"\$USER_ID\",\"resourceId\":\"\$SP_ID\",\"appRoleId\":\"$ADMIN_ROLE_ID\"}"
====================================================================
TXT

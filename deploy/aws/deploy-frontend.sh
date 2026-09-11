#!/usr/bin/env bash
# Publica el build de Angular en el bucket del sitio (ejecutar desde SkillLinkFrontend/)
set -euo pipefail
BUCKET="${1:?uso: deploy-frontend.sh <bucket-web> [api-base-url]}"
API_URL="${2:-}"
npx ng build --configuration production
DIST=dist/skilllink/browser
if [ -n "$API_URL" ]; then
  # config.json se sobreescribe con la URL del API Gateway y los datos del tenant (variables de entorno)
  cat > "$DIST/config.json" <<JSON
{
  "apiBaseUrl": "$API_URL",
  "auth": {
    "clientId": "${SPA_CLIENT_ID:-}",
    "tenantId": "${TENANT_ID:-}",
    "apiScope": "${API_SCOPE:-}"
  }
}
JSON
fi
aws s3 sync "$DIST" "s3://$BUCKET/" --delete --cache-control "public,max-age=60"
aws s3 cp "$DIST/config.json" "s3://$BUCKET/config.json" --cache-control "no-store"
echo "Publicado en s3://$BUCKET"

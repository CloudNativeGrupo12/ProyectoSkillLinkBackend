#!/usr/bin/env bash
# Arranca la API en modo local (H2 en archivo, sin Docker) validando tokens reales de Microsoft Entra ID.
# Los valores son identificadores publicos del tenant (no secretos); ver ../docs/AZURE-ENTRA-ID.md
set -euo pipefail
cd "$(dirname "$0")"
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-local}"
export JWT_ISSUER="${JWT_ISSUER:-https://login.microsoftonline.com/72fd0b5a-8a6a-4cff-89f6-bde961f7e250/v2.0}"
export JWT_AUDIENCE="${JWT_AUDIENCE:-393a4087-9e4d-41c4-a0ce-1755b1ba0d2f,api://393a4087-9e4d-41c4-a0ce-1755b1ba0d2f}"
export JWT_JWK_SET_URI="${JWT_JWK_SET_URI:-https://login.microsoftonline.com/72fd0b5a-8a6a-4cff-89f6-bde961f7e250/discovery/v2.0/keys}"
export CORS_ALLOWED_ORIGINS="${CORS_ALLOWED_ORIGINS:-http://localhost:4200}"
export APP_WRITE_SCOPE="${APP_WRITE_SCOPE:-skilllink.write}"
export APP_ADMIN_ROLE="${APP_ADMIN_ROLE:-ADMIN}"
echo "SkillLink API -> perfil $SPRING_PROFILES_ACTIVE | issuer $JWT_ISSUER"
./mvnw -q spring-boot:run

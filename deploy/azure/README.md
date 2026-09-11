# deploy/azure

`crear-app-registrations.sh`: crea `SkillLink-API` (scopes `skilllink.read`/`skilllink.write`, App Role `ADMIN`, tokens v2)
y `SkillLink-SPA` (cliente público con redirect URIs `http://localhost:4200` y el frontend publicado), pre-autoriza la SPA
en la API e imprime los valores para `skilllink.env` (backend) y `config.json` (frontend).

```bash
az login --use-device-code
FRONT_URL=https://<frontend-id>.execute-api.us-east-1.amazonaws.com ./crear-app-registrations.sh
```

Nunca se generan ni versionan `client_secret`: la SPA usa Authorization Code + PKCE.

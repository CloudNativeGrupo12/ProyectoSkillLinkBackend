# Arranca la API en modo local (H2, sin Docker) validando tokens reales de Microsoft Entra ID (Windows PowerShell).
Set-Location $PSScriptRoot
$env:SPRING_PROFILES_ACTIVE = "local"
$env:JWT_ISSUER = "https://login.microsoftonline.com/72fd0b5a-8a6a-4cff-89f6-bde961f7e250/v2.0"
$env:JWT_AUDIENCE = "393a4087-9e4d-41c4-a0ce-1755b1ba0d2f,api://393a4087-9e4d-41c4-a0ce-1755b1ba0d2f"
$env:JWT_JWK_SET_URI = "https://login.microsoftonline.com/72fd0b5a-8a6a-4cff-89f6-bde961f7e250/discovery/v2.0/keys"
$env:CORS_ALLOWED_ORIGINS = "http://localhost:4200"
$env:APP_WRITE_SCOPE = "skilllink.write"
$env:APP_ADMIN_ROLE = "ADMIN"
.\mvnw.cmd -q spring-boot:run

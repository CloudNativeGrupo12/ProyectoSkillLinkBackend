# Despliegue en AWS (Academy Learner Lab)

```text
Navegador ─HTTPS─► API Gateway "skilllink-frontend" ─► S3 website (Angular build)
Navegador ─HTTPS─► API Gateway "skilllink-api" (CORS) ─► EC2 :8080 Spring Boot ─► RDS PostgreSQL
```

CloudFront no está permitido en el laboratorio, por eso el frontend se sirve por HTTPS a través de un
segundo HTTP API (Entra ID exige redirect URIs `https`, salvo `localhost`).

## Recursos

| Recurso | Nombre | Notas |
|---|---|---|
| RDS PostgreSQL 16 | `skilllink-db` (db.t3.micro) | privado, SG `skilllink-db-sg` (5432 solo desde la EC2) |
| EC2 Amazon Linux 2023 | `skilllink-backend` (t3.small) | SG `skilllink-backend-sg` (8080/22), perfil `LabInstanceProfile` |
| S3 | `skilllink-artifacts-<cuenta>` | `backend/app.jar` y `backend/skilllink.env` |
| S3 | `skilllink-web-<cuenta>` | sitio estático (index.html como error document para rutas SPA) |
| API Gateway HTTP | `skilllink-api` | `ANY /{proxy+}` → `http://<ec2>:8080/{proxy}`; CORS configurado en el gateway |
| API Gateway HTTP | `skilllink-frontend` | `GET /{proxy+}` → sitio S3 |

## Ciclo de despliegue del backend (sin SSH)

La EC2 ejecuta un timer systemd (`skilllink-sync.timer`, cada 30 s) que compara el ETag de
`backend/app.jar` y `backend/skilllink.env` en S3; si cambian, los descarga y reinicia `skilllink.service`.

```bash
cd SkillLink && ./mvnw -DskipTests package
aws s3 cp target/SkillLink-0.0.1-SNAPSHOT.jar s3://skilllink-artifacts-<cuenta>/backend/app.jar
# variables (issuer, audience, cors...) -> editar y subir skilllink.env
aws s3 cp skilllink.env s3://skilllink-artifacts-<cuenta>/backend/skilllink.env
```

`deploy/aws/ec2-user-data.sh` contiene el bootstrap de la instancia; `deploy/aws/deploy-frontend.sh` publica el build de Angular.

## CORS en API Gateway

```bash
aws apigatewayv2 update-api --api-id <api-id> --cors-configuration \
  "AllowOrigins=https://<frontend>.execute-api.us-east-1.amazonaws.com,http://localhost:4200,\
AllowMethods=GET,POST,PUT,PATCH,DELETE,OPTIONS,AllowHeaders=Authorization,Content-Type,Accept,\
ExposeHeaders=Location,WWW-Authenticate,MaxAge=3600"
```

Con CORS configurado en un HTTP API, el gateway responde el preflight `OPTIONS` y agrega las cabeceras
`Access-Control-*`; el backend mantiene su propia política para el acceso directo (`CORS_ALLOWED_ORIGINS`).

## Verificación

```bash
curl -i https://<api-id>.execute-api.us-east-1.amazonaws.com/api/v1/public/health   # 200
curl -i https://<api-id>.execute-api.us-east-1.amazonaws.com/api/v1/auth/me         # 401
curl -i -X OPTIONS https://<api-id>.execute-api.us-east-1.amazonaws.com/api/v1/auth/me \
  -H "Origin: https://<frontend>.execute-api.us-east-1.amazonaws.com" -H "Access-Control-Request-Method: GET"
```

## Al reiniciar el Learner Lab

Cuando la sesión del laboratorio termina, AWS Academy cancela las credenciales (política `voc-cancel-cred`) y
detiene la EC2 (RDS también puede quedar detenida). Al volver a iniciar el lab:

1. Pegar las credenciales nuevas en `~/.aws/credentials`.
2. Arrancar la instancia si sigue detenida: `aws ec2 start-instances --instance-ids <id>`.
3. La IP pública cambia al detener/arrancar: asociar una Elastic IP (`aws ec2 allocate-address` + `associate-address`)
   y actualizar la integración del API Gateway `skilllink-api` con la nueva URI
   (`aws apigatewayv2 update-integration --api-id <api> --integration-id <int> --integration-uri http://<dns>:8080/{proxy}`).
4. Verificar `GET /api/v1/public/health` por el gateway y `GET /api/v1/public/info` (issuer/audience).
5. Publicar `config.json` del frontend con `clientId`, `tenantId` y `apiScope` (ver `docs/AZURE-ENTRA-ID.md`).

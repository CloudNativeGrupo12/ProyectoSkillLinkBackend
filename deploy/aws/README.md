# deploy/aws

- `ec2-user-data.sh`: bootstrap de la instancia backend (Java 21 + servicio systemd + sincronización desde S3).
- `deploy-frontend.sh`: build de Angular y publicación en S3 (con `config.json` apuntando al API Gateway).
- Detalle y comandos de API Gateway/CORS: `../../docs/AWS-DESPLIEGUE.md`.

Las credenciales de AWS nunca se versionan: se usan las del laboratorio en `~/.aws/credentials`.

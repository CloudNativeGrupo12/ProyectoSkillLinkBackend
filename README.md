# SkillLink — Backend Cloud Native

Plataforma de servicios y empleabilidad que conecta a trabajadores independientes y profesionales en Chile con clientes y reclutadores, priorizando la visibilidad del talento individual.

---

## Documentación Principal

- **[AGENTS.md](AGENTS.md)** — Contexto operativo para asistentes de IA: arquitectura en capas, regla de oro DTO, convenciones y estado actual.
- **[docs/CONTEXT.md](docs/CONTEXT.md)** — Documentación canónica: arquitectura, modelo de datos, reglas de negocio, flujos y roadmap.

Contenido clave disponible en la documentación:

- **Arquitectura Cloud Native:** Angular + AWS API Gateway + Spring Boot + PostgreSQL + Azure Entra ID (MSAL).
- **Modelo de Datos:** Diagramas Mermaid ER, descripción de tablas y script DDL de referencia en [docs/bd-ejemplo.sql](docs/bd-ejemplo.sql).
- **Reglas de Negocio:** Manejo de doble rol (Persona como Cliente y/o Trabajador), matriz de permisos y flujos críticos de usuario.
- **Directrices para IA:** Estructura en capas, convenciones JPA/Lombok, manejo de DTOs y validaciones.
- **Roadmap de Desarrollo:** Fases de implementación del backend.

---

## Documentación EV1 (DSY1107)

- [docs/EV1-MAPA-EVALUACION.md](docs/EV1-MAPA-EVALUACION.md) — mapa requisito → implementación y matriz de evidencia 200/401/403.
- [docs/AZURE-ENTRA-ID.md](docs/AZURE-ENTRA-ID.md) — App Registrations (API + SPA), scopes `skilllink.read/write`, App Role `ADMIN`.
- [docs/AWS-DESPLIEGUE.md](docs/AWS-DESPLIEGUE.md) — RDS + EC2 + S3 + API Gateway (CORS) y ciclo de despliegue.
- [docs/requests.http](docs/requests.http) — requests reutilizables para la evidencia.
- [docs/EVIDENCIA-E2E.md](docs/EVIDENCIA-E2E.md) — matriz observada con tokens reales de Entra ID.
- [docs/PRUEBA-EN-OTRA-MAQUINA.md](docs/PRUEBA-EN-OTRA-MAQUINA.md) — cómo clonar y probar todo en otra máquina (`run-local.sh`).

## Inicio Rápido (Backend)

El código fuente del backend Java se encuentra en la carpeta [`SkillLink/`](SkillLink):

```bash
cd SkillLink
./mvnw clean compile
# sin Docker (H2 en archivo) con el tenant real ya configurado:
./run-local.sh            # Windows: .\run-local.ps1
# con PostgreSQL:
docker compose up -d db && ./mvnw spring-boot:run
```

Variables de seguridad (ver `docs/AZURE-ENTRA-ID.md`): `JWT_ISSUER`, `JWT_AUDIENCE`, `APP_WRITE_SCOPE=skilllink.write`, `APP_ADMIN_ROLE=ADMIN`, `CORS_ALLOWED_ORIGINS`.

Para ejecutar las pruebas:

```bash
./mvnw test
```

### Tecnologías Principales

- **Java 21**
- **Spring Boot** (Spring Data JPA, Spring Security, Spring Web, Validation, WebFlux)
- **PostgreSQL** (en contenedor Docker)
- **Lombok**
- **Maven**

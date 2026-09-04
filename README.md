# SkillLink — Backend Cloud Native

Plataforma de servicios y empleabilidad que conecta a trabajadores independientes y profesionales en Chile con clientes y reclutadores, priorizando la visibilidad del talento individual.

---

## Documentación Principal

- **[AGENTS.md](file:///home/deymon/ProyectoSkillLinkBackend/AGENTS.md)** — Contexto operativo para asistentes de IA: arquitectura en capas, regla de oro DTO, convenciones y estado actual.
- **[docs/CONTEXT.md](file:///home/deymon/ProyectoSkillLinkBackend/docs/CONTEXT.md)** — Documentación canónica: arquitectura, modelo de datos, reglas de negocio, flujos y roadmap.

Contenido clave disponible en la documentación:

- **Arquitectura Cloud Native:** Angular + AWS API Gateway + Spring Boot + PostgreSQL + Azure Entra ID (MSAL).
- **Modelo de Datos:** Diagramas Mermaid ER, descripción de tablas y script DDL de referencia en [docs/bd-ejemplo.sql](file:///home/deymon/ProyectoSkillLinkBackend/docs/bd-ejemplo.sql).
- **Reglas de Negocio:** Manejo de doble rol (Persona como Cliente y/o Trabajador), matriz de permisos y flujos críticos de usuario.
- **Directrices para IA:** Estructura en capas, convenciones JPA/Lombok, manejo de DTOs y validaciones.
- **Roadmap de Desarrollo:** Fases de implementación del backend.

---

## Inicio Rápido (Backend)

El código fuente del backend Java se encuentra en la carpeta [`SkillLink/`](file:///home/deymon/ProyectoSkillLinkBackend/SkillLink):

```bash
cd SkillLink
./mvnw clean compile
```

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

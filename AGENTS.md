# AGENTS.md — Contexto para Asistentes de IA

Fuente de contexto sobre reglas de arquitectura, convenciones y estado del backend **SkillLink**.
Los asistentes de IA deben leer este archivo antes de generar o modificar código.
Documentación canónica extendida: [`docs/CONTEXT.md`](docs/CONTEXT.md).

---

## Proyecto

Marketplace chileno "worker-first": conecta trabajadores independientes (técnicos, artesanos, profesionales) con clientes. Prioriza el perfil del oferente (portafolio, certificaciones, comunas de cobertura, tarifas). Backend en `SkillLink/`.

## Stack (verificado en `pom.xml`)

- Java 21, Spring Boot **4.1.1**
- Spring Data JPA, Spring Security, Validation, WebMVC, WebFlux, RestClient, Lombok
- PostgreSQL (en contenedor Docker)
- Maven Wrapper (`mvnw`) — **los comandos se ejecutan con el directorio de trabajo en `./SkillLink`**

## Comandos

```bash
cd SkillLink
./mvnw clean compile     # compilar
./mvnw test              # pruebas
./mvnw spring-boot:run   # ejecutar
```

## Arquitectura en capas (paquete base `SkillLinkBackend.SkillLink`)

| Capa | Responsabilidad |
| :-- | :-- |
| `config/` | `@Configuration`, Spring Security (JWT Azure MSAL), CORS, Beans, OpenAPI |
| `controller/` | `@RestController` en `@RequestMapping("/api/v1/...")`. Recibe DTOs de `models/requests/`, responde con DTOs de `models/dto/` |
| `services/` | Interfaces + `@Service`/`@Transactional`. **Opera exclusivamente con DTOs**; contiene la conversión entidad↔DTO |
| `repositories/` | Interfaces que extienden `JpaRepository<Entity, ID>` |
| `models/entities/` | Entidades JPA. Nunca se exponen al exterior |
| `models/requests/` | DTOs de entrada: `Agregar*` (POST) y `Actualizar*` (PUT/PATCH), con `jakarta.validation` |
| `models/dto/` | DTOs de salida/transferencia para respuestas |
| `exceptions/` | `@RestControllerAdvice` con RFC 7807 (`ProblemDetail`) |

> [!IMPORTANT]
> **Regla de Oro (DTOs obligatorios):** NUNCA usar entidades de `models/entities/` en firmas de controladores o lógica expuesta de servicios, ni exponerlas al cliente. Los métodos reciben y devuelven siempre DTOs (`models/requests/` o `models/dto/`); la conversión con la entidad JPA se hace dentro del servicio.

## Convenciones de código

- **Lombok:** usar `@Getter`/`@Setter`. EVITAR `@Data` y `@ToString` en entidades con `@OneToMany`/`@ManyToMany` (bucles en `hashCode()`/`toString()`).
- **Fetch:** `FetchType.LAZY` por defecto en `@ManyToOne` y `@ManyToMany` (evitar N+1).
- **Tipos:** `BigDecimal` para precios (`NUMERIC(10,2)`); `LocalDate` para fechas; `LocalDateTime` para timestamps; campos JSON con Hibernate `JdbcTypeCode(SqlTypes.JSON)`.
- **Validaciones:** `jakarta.validation.constraints` (`@NotBlank`, `@NotNull`, `@Email`, `@PositiveOrZero`).
- **Seguridad:** validar JWT de Azure Entra ID; verificar que el usuario autenticado sea dueño del `Perfil`/`Publicacion` antes de `PUT`/`DELETE`.

## Modelo de datos (resumen)

Schema de referencia: `docs/bd-ejemplo.sql`

- `personas` → roles `clientes` / `trabajadores` (**multi-rol: una persona puede ser ambos**)
- `perfiles` (username único), `publicaciones` (precio_min/max, moneda, duración), `curriculums` (con `experiencia_json` JSONB), `certificaciones`
- `servicios` ↔ `categorias_servicio` (N:M), jerarquía geográfica `paises` → `regiones` → `ciudades` → `comunas` (N:M con clientes y trabajadores)
- `membresias` (Gratuito, Pro, Premium)

## Seguridad (EV1)

- `config/SecurityConfig.java`: Resource Server OAuth2 (firma + `iss` + `aud` + `exp`), `AuthoritiesConverter` (`scp`/`scope` → `SCOPE_*`, `roles` → `ROLE_*`), respuestas 401/403 en `application/problem+json`.
- Política: `/api/v1/public/**` y `GET` de catálogos públicos; escritura de recursos de usuario → `SCOPE_skilllink.write`; `/api/v1/admin/**` y escritura de catálogos maestros → `ROLE_ADMIN`; resto de `/api/**` autenticado.
- Propiedad del recurso: los servicios comparan el email del token con la `Persona` dueña (admin omite la verificación).
- Valores por entorno: `JWT_ISSUER`, `JWT_AUDIENCE`, `JWT_JWK_SET_URI`, `APP_WRITE_SCOPE`, `APP_ADMIN_ROLE`, `CORS_ALLOWED_ORIGINS` (ver `docs/AZURE-ENTRA-ID.md`).
- Modelo de lectura para la SPA: `services/CatalogoService` + `controller/CatalogoController` (`/api/v1/catalogo/**`), onboarding automático en `GET /api/v1/usuarios/me`.
- Matriz 200/401/403 automatizada: `src/test/java/.../SeguridadMatrizTests.java`.

## Perfiles de ejecución

| Perfil | Base de datos | Uso |
| :-- | :-- | :-- |
| `dev` (defecto) | PostgreSQL local (`docker compose up db`) | desarrollo |
| `local` | H2 en archivo `./data/` | desarrollo sin Docker |
| `prod` | PostgreSQL por variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) | AWS (RDS) |
| `test` | H2 en memoria | `./mvnw test` |

`DataSeeder` carga datos de demostración cuando la base está vacía (`APP_SEED_ENABLED`).

## Estado actual (verificado en el repo)

| Capa | Estado |
| :-- | :-- |
| `models/entities/` | ✅ 15 entidades JPA (incluye `SuscripcionInteres`) |
| `models/requests/` y `models/dto/` | ✅ completos (incluye `dto/catalogo/*` para la SPA) |
| `config/`, `controller/`, `services/`, `repositories/` | ✅ implementados |
| `exceptions/` | ✅ `GlobalExceptionHandler` RFC 7807 |
| `application*.properties` | ✅ perfiles `dev`, `local`, `prod`, `test` |
| Tests | ✅ `SkillLinkApplicationTests`, `SeguridadMatrizTests` (matriz 200/401/403) |
| Despliegue | ✅ `Dockerfile`, `docker-compose.yml`, `deploy/aws`, `deploy/azure`, `docs/AWS-DESPLIEGUE.md` |

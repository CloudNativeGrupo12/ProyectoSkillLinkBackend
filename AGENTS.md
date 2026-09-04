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

## Estado actual (verificado en el repo)

| Capa | Estado |
| :-- | :-- |
| `models/entities/` | ✅ 14 entidades JPA creadas (esqueleto) |
| `models/requests/` | ✅ DTOs `Agregar*`/`Actualizar*` para Persona, Cliente, Trabajador, Perfil, Publicacion, Servicio, CategoriaServicio, Curriculum, Certificacion, Membresia |
| `models/dto/` | ❌ **No existe** — debe crearse antes de implementar servicios |
| `config/`, `controller/`, `services/`, `repositories/` | ❌ Directorios vacíos |
| `exceptions/` | ❌ No existe el manejador global |
| `application.properties` | ⚠️ Solo `spring.application.name=SkillLink`; falta DataSource y perfiles (`dev`, `prod`) |
| Tests | ⚠️ Solo el test boilerplate `SkillLinkApplicationTests` |
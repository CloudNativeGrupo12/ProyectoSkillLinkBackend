# SkillLink — Documentación Integral del Proyecto & Contexto Técnico

> **Nota para Asistentes de Inteligencia Artificial (LLMs):**
> Este documento es la **fuente de verdad canónica** sobre la arquitectura, modelo de datos, reglas de negocio y estado de desarrollo del proyecto **SkillLink**. Antes de generar código se recomienda leer primero [`AGENTS.md`](../AGENTS.md) (contexto operativo de trabajo) y consultar esta documentación para el detalle de dominio.

---

## 1. Resumen Ejecutivo

**SkillLink** conecta a profesionales independientes, técnicos, artesanos y trabajadores con clientes que requieren servicios específicos. A diferencia de los Job Boards tradicionales, **prioriza la oferta del trabajador**: perfil enriquecido con portafolio, currículum, certificaciones verificables, cobertura geográfica por comunas y tarifas estimadas. Los clientes buscan, filtran y contactan según categoría técnica, ubicación y reputación.

**Contexto de mercado (Chile):** altas tasas de desocupación e informalidad laboral, dificultad de los trabajadores independientes para comercializar sus habilidades y falta de un directorio que certifique competencias de prestadores por comunas y regiones.

## 2. Arquitectura

Cloud Native y desacoplada, con cuatro componentes principales:

1. **Frontend (Angular):** SPA adaptativa (Desktop/Mobile) con modo oscuro. Autenticación en cliente con MSAL.
2. **Identidad (Azure Entra ID / MSAL):** OAuth2/OpenID Connect; el backend valida la firma y claims de los JWT.
3. **API Gateway (AWS):** Punto de entrada unificado con CORS, throttling y enrutamiento hacia el backend.
4. **Backend (Spring Boot, Java 21):** Arquitectura en capas con reglas de negocio y persistencia Spring Data JPA/Hibernate.
5. **Base de datos (PostgreSQL en Docker):** Relacional normalizado con `JSONB`, `DATE`, `TIMESTAMP`.

```
Angular → (token MSAL) → Azure Entra ID → AWS API Gateway → Controllers → Services → Repositories → PostgreSQL
```

## 3. Estructura de Directorios

```text
ProyectoSkillLinkBackend/
├── AGENTS.md                         # Contexto operativo para asistentes de IA
├── docs/                             # Documentación canónica (CONTEXT.md) y DDL de referencia
└── SkillLink/                        # Backend Spring Boot + Maven (directorio de trabajo para comandos)
    └── src/
        ├── main/java/SkillLinkBackend/SkillLink/
        │   ├── SkillLinkApplication.java   # @SpringBootApplication
        │   ├── config/                     # Seguridad (JWT Azure MSAL), CORS, Beans, OpenAPI
        │   ├── controller/                 # @RestController en /api/v1/...
        │   ├── services/                   # Lógica de negocio @Service/@Transactional (solo DTOs)
        │   ├── repositories/               # JpaRepository<Entity, ID>
        │   ├── exceptions/                 # @RestControllerAdvice RFC 7807 (ProblemDetail)
        │   └── models/
        │       ├── entities/               # Entidades JPA (nunca expuestas)
        │       ├── requests/               # DTOs de entrada Agregar*/Actualizar* (jakarta.validation)
        │       └── dto/                    # DTOs de salida/transferencia
        ├── main/resources/application.properties
        └── test/
```

## 4. Modelo de Datos

Schema de referencia: `docs/bd-ejemplo.sql`

| Dominio | Tablas | Notas |
| :-- | :-- | :-- |
| Usuarios | `personas`, `clientes`, `trabajadores` | `personas` = datos biográficos y email único. **Multi-rol**: una persona puede ser cliente y trabajador a la vez. |
| Perfil y oferta | `perfiles`, `publicaciones`, `curriculums` | `perfiles` con `username` único; `publicaciones` con `precio_min`/`precio_max`, moneda y duración; `curriculums` con `experiencia_json` (JSONB). |
| Catálogo | `servicios`, `categorias_servicio`, `servicios_categorias` | Servicios N:M con categorías (ej: Construcción y Hogar, Tecnología y Software). |
| Geografía | `paises`, `regiones`, `ciudades`, `comunas` | Jerarquía normalizada; N:M tanto con clientes como con trabajadores. |
| Acreditación | `certificaciones`, `trabajadores_certificaciones` | Entidad emisora, fecha y URL pública de verificación. |
| Planes | `membresias` | Gratuito, Pro, Premium. Determina límites de publicaciones y visibilidad. |

## 5. Reglas de Negocio y Roles

### Multi-rol
Un usuario puede ser **Trabajador y Cliente simultáneamente**: `personas` (entidad de contacto) se separa de los roles transaccionales `clientes` y `trabajadores`.

### Matriz de roles y permisos

| Rol | Acciones permitidas | Prohibiciones |
| :-- | :-- | :-- |
| Anónimo | Ver landing, catálogo, buscar/filtrar publicaciones, perfiles públicos resumidos | Sin teléfonos/emails de contacto, sin publicar, sin contratar/calificar |
| Cliente | Búsqueda extendida, contacto con trabajadores, cotizar/contratar, calificar y reseñar | Publicar ofertas a menos que active rol de trabajador |
| Trabajador | Crear/publicar/pausar `publicaciones`, gestionar curriculum y certificaciones, asignar comunas y tarifas | Alterar catálogos maestros; validar sus certificaciones sin URL/moderación |
| Administrador | Moderar publicaciones/perfiles, validar certificaciones, administrar catálogo maestro | Acciones sujetas a auditoría |

## 6. Flujos Operativos Críticos

1. **Registro/Onboarding:** el usuario se autentica con Azure Entra ID (MSAL) → backend verifica `personas` por email y crea la persona con membresía básica si no existe → el usuario elige perfil Trabajador y/o Cliente → se genera el `Perfil` con username y descripción.
2. **Publicación de servicio:** el trabajador selecciona `Servicio` + `Comunas` de atención → ingresa banda de precios (CLP), moneda y duración → asocia curriculum/certificaciones → se persiste `publicaciones` en estado activo.
3. **Búsqueda/descubrimiento:** el cliente filtra por categoría, ubicación (comuna/región) y rango de precio → el backend ejecuta consulta filtrada (índices `idx_servicios_nombre`, `idx_categorias_servicio_nombre`) → tarjetas de profesionales con rating, tarifas y badges de certificación.
4. **Contacto/contratación:** el cliente abre el detalle de la publicación → si está autenticado se habilitan vías de contacto seguras/mensajería → se registra la interacción para métricas y seguridad.

## 7. Convenciones para Asistentes de IA

### Regla de Oro (DTOs obligatorios)
> [!CAUTION]
> **NUNCA** usar entidades de `models/entities/` en firmas de controladores o lógica expuesta de servicios, ni exponerlas al cliente. Los métodos reciben y devuelven siempre DTOs (`models/requests/` o `models/dto/`). La conversión entidad↔DTO se realiza dentro del servicio.

### Capas
- `config/` → `@Configuration`, Spring Security (JWT Azure MSAL), CORS, Beans, OpenAPI.
- `controller/` → `@RestController` en `@RequestMapping("/api/v1/...")`; recibe `models/requests/`, responde `models/dto/`.
- `services/` → interfaces + `@Service`/`@Transactional`; operan estrictamente con DTOs.
- `repositories/` → extend `JpaRepository<Entity, ID>`.
- `exceptions/` → `@RestControllerAdvice` con RFC 7807 (`ProblemDetail`).

### JPA y Lombok
- `@Getter`/`@Setter`; **evitar** `@Data` y `@ToString` en relaciones `@OneToMany`/`@ManyToMany`.
- `FetchType.LAZY` en `@ManyToOne` y `@ManyToMany`.
- `BigDecimal` para precios (`NUMERIC(10,2)`); `LocalDate` fechas; `LocalDateTime` timestamps; JSON con `JdbcTypeCode(SqlTypes.JSON)`.

### Validaciones y seguridad
- `jakarta.validation.constraints` (`@NotBlank`, `@NotNull`, `@Email`, `@PositiveOrZero`).
- Validar JWT emitidos por Azure Entra ID; verificar que el usuario autenticado sea dueño del `Perfil`/`Publicacion` antes de `PUT`/`DELETE`.

## 8. Estado Actual y Roadmap

### Estado actual (verificado)
| Capa | Estado |
| :-- | :-- |
| `models/entities/` | ✅ 14 entidades JPA (esqueleto) |
| `models/requests/` | ✅ DTOs `Agregar*`/`Actualizar*` (Persona, Cliente, Trabajador, Perfil, Publicacion, Servicio, CategoriaServicio, Curriculum, Certificacion, Membresia) |
| `models/dto/` | ❌ No existe — debe crearse antes de implementar servicios |
| `config/`, `controller/`, `services/`, `repositories/` | ❌ Vacías |
| `exceptions/` | ❌ No existe el manejador global |
| `application.properties` | ⚠️ Solo `spring.application.name`; falta DataSource y perfiles (`dev`, `prod`) |
| Tests | ⚠️ Solo `SkillLinkApplicationTests` boilerplate |

### Roadmap
1. **Fase 1 — Mapeo JPA:** completar atributos, anotaciones y relaciones en las 14 entidades según `docs/bd-ejemplo.sql`; configurar `application.properties` con DataSource PostgreSQL y perfiles (`dev`, `prod`, `test`).
2. **Fase 2 — Repositorios y servicios core:** `JpaRepository` para entidades principales; crear `models/dto/` e implementar servicios transaccionales.
3. **Fase 3 — Controladores REST:** endpoints para registro, catálogo, búsqueda de publicaciones y gestión de perfiles; `GlobalExceptionHandler` con `ProblemDetail`.
4. **Fase 4 — Seguridad y gateway:** Spring Security OAuth2 Resource Server para validar tokens de Azure MSAL; CORS y despliegue tras AWS API Gateway.
5. **Fase 5 — Containerización y CI/CD:** `Dockerfile` multi-stage y `docker-compose.yml` para Backend + PostgreSQL.
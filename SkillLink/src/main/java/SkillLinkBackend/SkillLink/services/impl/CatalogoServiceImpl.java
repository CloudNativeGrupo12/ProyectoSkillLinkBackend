package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.catalogo.CategoriaCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.CertificacionPublicaDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.PerfilPublicoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.ProfesionalCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.PublicacionCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.UsuarioActualDto;
import SkillLinkBackend.SkillLink.models.entities.CategoriaServicio;
import SkillLinkBackend.SkillLink.models.entities.Certificacion;
import SkillLinkBackend.SkillLink.models.entities.Cliente;
import SkillLinkBackend.SkillLink.models.entities.Comuna;
import SkillLinkBackend.SkillLink.models.entities.Curriculum;
import SkillLinkBackend.SkillLink.models.entities.Membresia;
import SkillLinkBackend.SkillLink.models.entities.Perfil;
import SkillLinkBackend.SkillLink.models.entities.Persona;
import SkillLinkBackend.SkillLink.models.entities.Publicacion;
import SkillLinkBackend.SkillLink.models.entities.Trabajador;
import SkillLinkBackend.SkillLink.models.requests.PublicarServicioRequest;
import SkillLinkBackend.SkillLink.repositories.CategoriaServicioRepository;
import SkillLinkBackend.SkillLink.repositories.CertificacionRepository;
import SkillLinkBackend.SkillLink.repositories.ClienteRepository;
import SkillLinkBackend.SkillLink.repositories.CurriculumRepository;
import SkillLinkBackend.SkillLink.repositories.MembresiaRepository;
import SkillLinkBackend.SkillLink.repositories.PerfilRepository;
import SkillLinkBackend.SkillLink.repositories.PersonaRepository;
import SkillLinkBackend.SkillLink.repositories.PublicacionRepository;
import SkillLinkBackend.SkillLink.repositories.TrabajadorRepository;
import SkillLinkBackend.SkillLink.services.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private static final String MEMBRESIA_BASE = "Gratuito";
    private static final String TIPO_OFERTA = "OFERTA_SERVICIO";

    private final CategoriaServicioRepository categoriaRepository;
    private final PublicacionRepository publicacionRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final PerfilRepository perfilRepository;
    private final CertificacionRepository certificacionRepository;
    private final CurriculumRepository curriculumRepository;
    private final PersonaRepository personaRepository;
    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;

    // ------------------------------------------------------------------ catalogo publico

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaCatalogoDto> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .sorted(Comparator.comparing(CategoriaServicio::getId))
                .map(this::aCategoria)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaCatalogoDto> obtenerCategoria(Long id) {
        return categoriaRepository.findById(id).map(this::aCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionCatalogoDto> listarPublicaciones(Long categoriaId, Long trabajadorId,
                                                            boolean soloDestacadas, Integer limite) {
        List<Publicacion> publicaciones;
        if (trabajadorId != null) {
            publicaciones = publicacionRepository.findByPerfilTrabajadorIdOrderByCreadoEnDesc(trabajadorId);
        } else if (categoriaId != null) {
            publicaciones = publicacionRepository.findByPerfilCategoriaServicioIdOrderByCalificacionPromedioDesc(categoriaId);
        } else {
            publicaciones = publicacionRepository.findAllByOrderByCalificacionPromedioDesc();
        }
        var stream = publicaciones.stream().filter(p -> "activo".equalsIgnoreCase(p.getEstado()) || trabajadorId != null);
        if (soloDestacadas) {
            stream = stream.sorted(Comparator.comparing(
                    (Publicacion p) -> p.getCalificacionPromedio() == null ? BigDecimal.ZERO : p.getCalificacionPromedio())
                    .reversed());
        }
        if (limite != null && limite > 0) {
            stream = stream.limit(limite);
        }
        return stream.map(this::aPublicacion).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PublicacionCatalogoDto obtenerPublicacion(Long id) {
        return publicacionRepository.findById(id).map(this::aPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicacion no encontrada con id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesionalCatalogoDto> listarProfesionales(boolean soloDestacados) {
        var stream = trabajadorRepository.findAll().stream()
                .map(this::aProfesional)
                .filter(p -> p.perfilId() != null);
        if (soloDestacados) {
            stream = stream.sorted(Comparator.comparing(ProfesionalCatalogoDto::rating).reversed()).limit(6);
        } else {
            stream = stream.sorted(Comparator.comparing(ProfesionalCatalogoDto::id));
        }
        return stream.toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfesionalCatalogoDto> obtenerProfesional(Long trabajadorId) {
        return trabajadorRepository.findById(trabajadorId).map(this::aProfesional);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PerfilPublicoDto> obtenerPerfilPorTrabajador(Long trabajadorId) {
        return perfilRepository.findFirstByTrabajadorId(trabajadorId).map(this::aPerfilPublico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionPublicaDto> listarCertificacionesPorTrabajador(Long trabajadorId) {
        return certificacionRepository.findByPerfilTrabajadorId(trabajadorId).stream()
                .map(c -> new CertificacionPublicaDto(c.getId(), c.getNombre(), c.getEntidadEmisora(),
                        c.getFechaEmision(), c.getUrlVerificacion(), trabajadorId))
                .toList();
    }

    // ------------------------------------------------------------------ usuario autenticado

    @Override
    @Transactional
    public UsuarioActualDto usuarioActual() {
        return resolverUsuario(null);
    }

    @Override
    @Transactional
    public PublicacionCatalogoDto publicarServicio(PublicarServicioRequest request) {
        UsuarioActualDto usuario = resolverUsuario(request.getNombreAutor());
        Perfil perfil = perfilRepository.findById(usuario.perfilId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + usuario.perfilId()));
        CategoriaServicio categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada con id " + request.getCategoriaId()));
        if (perfil.getCategoriaServicio() == null) {
            perfil.setCategoriaServicio(categoria);
        }

        Publicacion publicacion = new Publicacion();
        publicacion.setTipoPublicacion(TIPO_OFERTA);
        publicacion.setTitulo(request.getTitulo().trim());
        publicacion.setDescripcion(request.getDescripcion().trim());
        publicacion.setEstado("activo");
        publicacion.setPerfil(perfil);
        publicacion.setCurriculum(curriculumRepository.findByPerfilId(perfil.getId()).orElse(null));
        publicacion.setPrecioMin(request.getPrecio());
        publicacion.setPrecioMax(request.getPrecio());
        publicacion.setTipoPrecio(request.getPrecio());
        publicacion.setModalidadPrecio(valorODefecto(request.getModalidadPrecio(), "por servicio"));
        publicacion.setMoneda("CLP");
        publicacion.setDuracionEstimada(valorODefecto(request.getDuracionEstimada(), "A convenir"));
        publicacion.setCalificacionPromedio(BigDecimal.ZERO);
        publicacion.setTotalResenas(0);
        Publicacion guardada = publicacionRepository.save(publicacion);
        // la publicacion se asocia a la categoria del perfil; si el perfil ya tenia otra, se conserva
        return aPublicacion(guardada, categoria);
    }

    @Override
    @Transactional
    public void eliminarPublicacion(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicacion no encontrada con id " + id));
        if (!SecurityUtils.esAdmin()) {
            String email = SecurityUtils.emailObligatorio();
            Trabajador trabajador = publicacion.getPerfil().getTrabajador();
            if (trabajador == null || !email.equals(trabajador.getPersona().getEmail().toLowerCase())) {
                throw new AccionNoPermitidaException("Solo el dueno de la publicacion o un administrador puede eliminarla.");
            }
        }
        publicacionRepository.delete(publicacion);
    }

    /**
     * Onboarding: el IDaaS autentica; el dominio registra a la persona la primera vez
     * (persona + cliente + trabajador + perfil con membresia base).
     */
    private UsuarioActualDto resolverUsuario(String nombreSugerido) {
        String email = SecurityUtils.emailObligatorio();
        Jwt jwt = SecurityUtils.jwtActual().orElse(null);
        boolean creado = false;
        Persona existente = personaRepository.findByEmailConRoles(email).orElse(null);
        if (existente == null) {
            existente = crearPersona(email, jwt, nombreSugerido);
            creado = true;
        }
        final Persona persona = existente;
        Membresia membresia = persona.getMembresia();
        Cliente cliente = clienteRepository.findByPersonaId(persona.getId()).stream().findFirst()
                .orElseGet(() -> crearCliente(persona, membresia));
        Trabajador trabajador = trabajadorRepository.findByPersonaId(persona.getId()).stream().findFirst()
                .orElseGet(() -> crearTrabajador(persona, membresia));
        Perfil perfil = perfilRepository.findFirstByTrabajadorId(trabajador.getId())
                .orElseGet(() -> crearPerfil(email, cliente, trabajador));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = auth == null ? List.of()
                : auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        List<String> roles = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .toList();

        return new UsuarioActualDto(
                "trabajador",
                persona.getId(),
                cliente.getId(),
                trabajador.getId(),
                perfil.getId(),
                persona.getPNombre(),
                persona.getApPaterno(),
                persona.getEmail(),
                perfil.getUsername(),
                roles,
                authorities,
                creado);
    }

    private Persona crearPersona(String email, Jwt jwt, String nombreSugerido) {
        String nombre = claim(jwt, "given_name");
        String apellido = claim(jwt, "family_name");
        if ((nombre == null || apellido == null) && nombreSugerido != null && !nombreSugerido.isBlank()) {
            String[] partes = nombreSugerido.trim().split("\\s+", 2);
            nombre = nombre == null ? partes[0] : nombre;
            apellido = apellido == null ? (partes.length > 1 ? partes[1] : "") : apellido;
        }
        if (nombre == null || nombre.isBlank()) {
            String nombreCompleto = claim(jwt, "name");
            if (nombreCompleto != null && !nombreCompleto.isBlank()) {
                String[] partes = nombreCompleto.trim().split("\\s+", 2);
                nombre = partes[0];
                if (apellido == null || apellido.isBlank()) {
                    apellido = partes.length > 1 ? partes[1] : "";
                }
            }
        }
        if (nombre == null || nombre.isBlank()) {
            nombre = email.substring(0, email.indexOf('@') > 0 ? email.indexOf('@') : email.length());
        }
        Persona persona = new Persona();
        persona.setPNombre(recortar(nombre, 50));
        persona.setApPaterno(recortar(apellido == null || apellido.isBlank() ? "-" : apellido, 50));
        persona.setApMaterno("-");
        persona.setEmail(email);
        persona.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        persona.setMembresia(membresiaBase());
        return personaRepository.save(persona);
    }

    private Cliente crearCliente(Persona persona, Membresia membresia) {
        Cliente cliente = new Cliente();
        cliente.setPersona(persona);
        cliente.setMembresia(membresia);
        return clienteRepository.save(cliente);
    }

    private Trabajador crearTrabajador(Persona persona, Membresia membresia) {
        Trabajador trabajador = new Trabajador();
        trabajador.setPersona(persona);
        trabajador.setMembresia(membresia);
        return trabajadorRepository.save(trabajador);
    }

    private Perfil crearPerfil(String email, Cliente cliente, Trabajador trabajador) {
        String base = email.substring(0, email.indexOf('@') > 0 ? email.indexOf('@') : email.length())
                .replaceAll("[^a-zA-Z0-9._-]", "").toLowerCase();
        if (base.isBlank()) {
            base = "usuario";
        }
        String username = recortar(base, 40);
        int sufijo = 1;
        while (perfilRepository.existsByUsername(username)) {
            username = recortar(base, 40) + "." + (sufijo++);
        }
        Perfil perfil = new Perfil();
        perfil.setUsername(username);
        perfil.setCliente(cliente);
        perfil.setTrabajador(trabajador);
        perfil.setDescripcion("Perfil creado automaticamente al iniciar sesion.");
        return perfilRepository.save(perfil);
    }

    private Membresia membresiaBase() {
        return membresiaRepository.findFirstByNombreIgnoreCase(MEMBRESIA_BASE)
                .or(() -> membresiaRepository.findAll().stream().findFirst())
                .orElseGet(() -> {
                    Membresia m = new Membresia();
                    m.setNombre(MEMBRESIA_BASE);
                    return membresiaRepository.save(m);
                });
    }

    // ------------------------------------------------------------------ mapeos

    private CategoriaCatalogoDto aCategoria(CategoriaServicio c) {
        return new CategoriaCatalogoDto(c.getId(), c.getNombre(), c.getDescripcion(), c.getIcono(),
                publicacionRepository.countByPerfilCategoriaServicioId(c.getId()));
    }

    private PublicacionCatalogoDto aPublicacion(Publicacion p) {
        CategoriaServicio categoria = p.getPerfil() != null ? p.getPerfil().getCategoriaServicio() : null;
        if (categoria == null && p.getServicio() != null && !p.getServicio().getCategorias().isEmpty()) {
            categoria = p.getServicio().getCategorias().get(0);
        }
        return aPublicacion(p, categoria);
    }

    private PublicacionCatalogoDto aPublicacion(Publicacion p, CategoriaServicio categoria) {
        Perfil perfil = p.getPerfil();
        Trabajador trabajador = perfil != null ? perfil.getTrabajador() : null;
        Persona persona = trabajador != null ? trabajador.getPersona() : null;
        String nombre = p.getTitulo() != null && !p.getTitulo().isBlank() ? p.getTitulo() : p.getTipoPublicacion();
        return new PublicacionCatalogoDto(
                p.getId(),
                nombre,
                p.getDescripcion(),
                categoria != null ? categoria.getId() : null,
                categoria != null ? categoria.getNombre() : "General",
                trabajador != null ? trabajador.getId() : null,
                perfil != null ? perfil.getId() : null,
                persona != null ? (persona.getPNombre() + " " + persona.getApPaterno()).trim() : "Profesional",
                ubicacion(trabajador),
                p.getPrecioMin(),
                p.getPrecioMax(),
                valorODefecto(p.getModalidadPrecio(), "por servicio"),
                p.getMoneda(),
                valorODefecto(p.getEstado(), "activo"),
                p.getCalificacionPromedio() == null ? BigDecimal.ZERO : p.getCalificacionPromedio(),
                p.getTotalResenas() == null ? 0 : p.getTotalResenas(),
                p.getCreadoEn());
    }

    private ProfesionalCatalogoDto aProfesional(Trabajador t) {
        Persona persona = t.getPersona();
        Perfil perfil = perfilRepository.findFirstByTrabajadorId(t.getId()).orElse(null);
        List<Publicacion> publicaciones = publicacionRepository.findByPerfilTrabajadorIdOrderByCreadoEnDesc(t.getId());
        int reviews = publicaciones.stream().mapToInt(p -> p.getTotalResenas() == null ? 0 : p.getTotalResenas()).sum();
        BigDecimal rating = BigDecimal.ZERO;
        if (reviews > 0) {
            BigDecimal suma = publicaciones.stream()
                    .filter(p -> p.getTotalResenas() != null && p.getTotalResenas() > 0 && p.getCalificacionPromedio() != null)
                    .map(p -> p.getCalificacionPromedio().multiply(BigDecimal.valueOf(p.getTotalResenas())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            rating = suma.divide(BigDecimal.valueOf(reviews), 1, RoundingMode.HALF_UP);
        }
        BigDecimal precio = publicaciones.stream()
                .map(Publicacion::getPrecioMin)
                .filter(v -> v != null)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        List<Certificacion> certificaciones = perfil != null ? certificacionRepository.findByPerfilId(perfil.getId()) : List.of();
        boolean verificado = !certificaciones.isEmpty() || !t.getCertificaciones().isEmpty();
        CategoriaServicio categoria = perfil != null ? perfil.getCategoriaServicio() : null;
        String rol = perfil != null && perfil.getTituloProfesional() != null && !perfil.getTituloProfesional().isBlank()
                ? perfil.getTituloProfesional()
                : (categoria != null ? categoria.getNombre() : "Profesional independiente");
        Comuna comuna = t.getComunas().isEmpty() ? null : t.getComunas().get(0);
        return new ProfesionalCatalogoDto(
                t.getId(),
                perfil != null ? perfil.getId() : null,
                perfil != null ? perfil.getUsername() : null,
                persona.getPNombre(),
                persona.getApPaterno(),
                rol,
                comuna != null ? comuna.getCiudad().getNombre() : "Chile",
                comuna != null ? comuna.getNombre() : "",
                rating,
                reviews,
                precio,
                t.getMembresia() != null ? t.getMembresia().getId() : null,
                verificado,
                categoria != null ? categoria.getId() : null);
    }

    private PerfilPublicoDto aPerfilPublico(Perfil perfil) {
        Trabajador trabajador = perfil.getTrabajador();
        Persona persona = trabajador != null ? trabajador.getPersona() : null;
        Curriculum curriculum = curriculumRepository.findByPerfilId(perfil.getId()).orElse(null);
        String experiencia = perfil.getAniosExperiencia() != null
                ? perfil.getAniosExperiencia() + (perfil.getAniosExperiencia() == 1 ? " ano" : " anos")
                : "Sin informacion";
        return new PerfilPublicoDto(
                perfil.getId(),
                trabajador != null ? trabajador.getId() : null,
                perfil.getUsername(),
                perfil.getDescripcion(),
                perfil.getTituloProfesional(),
                perfil.getCategoriaServicio() != null ? perfil.getCategoriaServicio().getId() : null,
                persona != null ? persona.getEmail() : null,
                persona != null ? persona.getTelefono() : null,
                experiencia,
                curriculum != null ? curriculum.getResumen() : null);
    }

    private String ubicacion(Trabajador trabajador) {
        if (trabajador == null || trabajador.getComunas().isEmpty()) {
            return "Chile";
        }
        Comuna comuna = trabajador.getComunas().get(0);
        return comuna.getCiudad().getNombre() + ", " + comuna.getNombre();
    }

    private static String claim(Jwt jwt, String nombre) {
        if (jwt == null) {
            return null;
        }
        Object valor = jwt.getClaim(nombre);
        return valor == null ? null : valor.toString();
    }

    private static String valorODefecto(String valor, String defecto) {
        return valor == null || valor.isBlank() ? defecto : valor;
    }

    private static String recortar(String valor, int max) {
        return valor.length() <= max ? valor : valor.substring(0, max);
    }
}

package SkillLinkBackend.SkillLink.config;

import SkillLinkBackend.SkillLink.models.entities.CategoriaServicio;
import SkillLinkBackend.SkillLink.models.entities.Certificacion;
import SkillLinkBackend.SkillLink.models.entities.Ciudad;
import SkillLinkBackend.SkillLink.models.entities.Cliente;
import SkillLinkBackend.SkillLink.models.entities.Comuna;
import SkillLinkBackend.SkillLink.models.entities.Curriculum;
import SkillLinkBackend.SkillLink.models.entities.Membresia;
import SkillLinkBackend.SkillLink.models.entities.Pais;
import SkillLinkBackend.SkillLink.models.entities.Perfil;
import SkillLinkBackend.SkillLink.models.entities.Persona;
import SkillLinkBackend.SkillLink.models.entities.Publicacion;
import SkillLinkBackend.SkillLink.models.entities.Region;
import SkillLinkBackend.SkillLink.models.entities.Servicio;
import SkillLinkBackend.SkillLink.models.entities.Trabajador;
import SkillLinkBackend.SkillLink.repositories.CategoriaServicioRepository;
import SkillLinkBackend.SkillLink.repositories.CertificacionRepository;
import SkillLinkBackend.SkillLink.repositories.CiudadRepository;
import SkillLinkBackend.SkillLink.repositories.ClienteRepository;
import SkillLinkBackend.SkillLink.repositories.ComunaRepository;
import SkillLinkBackend.SkillLink.repositories.CurriculumRepository;
import SkillLinkBackend.SkillLink.repositories.MembresiaRepository;
import SkillLinkBackend.SkillLink.repositories.PaisRepository;
import SkillLinkBackend.SkillLink.repositories.PerfilRepository;
import SkillLinkBackend.SkillLink.repositories.PersonaRepository;
import SkillLinkBackend.SkillLink.repositories.PublicacionRepository;
import SkillLinkBackend.SkillLink.repositories.RegionRepository;
import SkillLinkBackend.SkillLink.repositories.ServicioRepository;
import SkillLinkBackend.SkillLink.repositories.TrabajadorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Datos de demostracion persistidos en la base (no son mocks del frontend).
 * Solo se cargan cuando la tabla de membresias esta vacia.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final MembresiaRepository membresiaRepository;
    private final PaisRepository paisRepository;
    private final RegionRepository regionRepository;
    private final CiudadRepository ciudadRepository;
    private final ComunaRepository comunaRepository;
    private final CategoriaServicioRepository categoriaRepository;
    private final ServicioRepository servicioRepository;
    private final PersonaRepository personaRepository;
    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final PerfilRepository perfilRepository;
    private final CurriculumRepository curriculumRepository;
    private final CertificacionRepository certificacionRepository;
    private final PublicacionRepository publicacionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (membresiaRepository.count() > 0) {
            log.info("Datos de demostracion ya presentes; se omite el seed.");
            return;
        }
        log.info("Cargando datos de demostracion de SkillLink...");

        Membresia gratuito = membresia("Gratuito");
        Membresia premium = membresia("Premium");
        membresia("Pro");

        Pais chile = new Pais();
        chile.setNombre("Chile");
        paisRepository.save(chile);
        Region rm = region("Region Metropolitana", chile);
        Region valpo = region("Region de Valparaiso", chile);
        Region biobio = region("Region del Biobio", chile);
        Ciudad santiago = ciudad("Santiago", rm);
        Ciudad valparaiso = ciudad("Valparaiso", valpo);
        Ciudad concepcion = ciudad("Concepcion", biobio);
        Comuna lasCondes = comuna("Las Condes", santiago);
        Comuna providencia = comuna("Providencia", santiago);
        Comuna maipu = comuna("Maipu", santiago);
        Comuna vina = comuna("Vina del Mar", valparaiso);
        Comuna conce = comuna("Concepcion", concepcion);

        List<CategoriaServicio> categorias = new ArrayList<>();
        String[][] cats = {
                {"Fitness", "Entrenadores personales, nutricionistas y coaches deportivos", "fitness"},
                {"Programacion", "Desarrolladores web, movil y software", "programacion"},
                {"Gasfiteria", "Instalacion y reparacion de sistemas de agua", "gasfiteria"},
                {"Electricidad", "Instalaciones electricas y mantenimiento", "electricidad"},
                {"Fotografia", "Fotografos profesionales para eventos y productos", "fotografia"},
                {"Carpinteria", "Muebles a medida y reparaciones", "carpinteria"},
                {"Marketing Digital", "Especialistas en redes sociales y publicidad online", "marketing"},
                {"Diseno Grafico", "Disenadores de logos, branding y contenido visual", "diseno"},
                {"Clases Particulares", "Profesores de matematicas, idiomas y mas", "clases"},
                {"Belleza", "Peluqueros, maquilladores y estilistas", "belleza"},
                {"Limpieza", "Servicios de limpieza para hogar y oficina", "limpieza"},
                {"Gaming", "Coaches de videojuegos y streamers", "gaming"},
        };
        for (String[] c : cats) {
            CategoriaServicio categoria = new CategoriaServicio();
            categoria.setNombre(c[0]);
            categoria.setDescripcion(c[1]);
            categoria.setIcono(c[2]);
            categorias.add(categoriaRepository.save(categoria));
        }

        // Catalogo maestro de servicios (N:M con categorias)
        Map<String, Integer> serviciosPorCategoria = Map.ofEntries(
                Map.entry("Entrenamiento personal", 0), Map.entry("Nutricion deportiva", 0),
                Map.entry("Desarrollo web", 1), Map.entry("Desarrollo de apps moviles", 1),
                Map.entry("Reparacion de canerias", 2), Map.entry("Instalacion electrica", 3),
                Map.entry("Fotografia de eventos", 4), Map.entry("Sesion fotografica", 4),
                Map.entry("Muebles a medida", 5), Map.entry("Gestion de redes sociales", 6),
                Map.entry("Identidad de marca", 7), Map.entry("Clases de matematicas", 8));
        for (Map.Entry<String, Integer> e : serviciosPorCategoria.entrySet()) {
            Servicio servicio = new Servicio();
            servicio.setNombre(e.getKey());
            servicio.setDescripcion("Servicio de " + e.getKey().toLowerCase() + " ofrecido por profesionales de SkillLink.");
            servicio.setActivo(true);
            servicio.getCategorias().add(categorias.get(e.getValue()));
            servicioRepository.save(servicio);
        }

        // Profesionales (persona + cliente + trabajador + perfil + curriculum)
        Object[][] pros = {
                // nombre, apPaterno, apMaterno, email, telefono, membresia, comuna, categoriaIdx, username, titulo, anios, descripcion, resumen
                {"Carlos", "Mendoza", "Rojas", "carlos.mendoza@skilllink.cl", "+56 9 1234 5678", gratuito, lasCondes, 0, "carlos.mendoza", "Entrenador Personal", 8,
                        "Entrenador personal certificado con mas de 8 anos de experiencia ayudando a personas a alcanzar sus metas fitness. Especializado en entrenamiento funcional y preparacion deportiva.",
                        "Profesional del fitness con certificaciones internacionales. Experiencia en gimnasios de alto rendimiento y entrenamiento personalizado a domicilio."},
                {"Maria", "Gonzalez", "Perez", "maria.gonzalez@skilllink.cl", "+56 9 2345 6789", gratuito, vina, 1, "maria.gonzalez", "Desarrolladora Full Stack", 6,
                        "Desarrolladora Full Stack con pasion por crear soluciones tecnologicas innovadoras. Dominio de Angular, Spring Boot y arquitectura cloud.",
                        "Ingeniera informatica con experiencia en startups y empresas tech. Especialista en desarrollo web moderno y metodologias agiles."},
                {"Roberto", "Silva", "Munoz", "roberto.silva@skilllink.cl", "+56 9 3456 7890", gratuito, providencia, 4, "roberto.silva", "Fotografo de Eventos", 10,
                        "Fotografo profesional especializado en matrimonios, eventos corporativos y retratos.",
                        "Mas de una decada cubriendo eventos sociales y corporativos en todo Chile."},
                {"Andrea", "Torres", "Lagos", "andrea.torres@skilllink.cl", "+56 9 4567 8901", premium, conce, 7, "andrea.torres", "Disenadora Grafica", 5,
                        "Disenadora grafica enfocada en identidad de marca y contenido visual para emprendedores.",
                        "Licenciada en diseno con portafolio en branding, packaging y redes sociales."},
                {"Luis", "Ramirez", "Soto", "luis.ramirez@skilllink.cl", "+56 9 5678 9012", premium, maipu, 3, "luis.ramirez", "Electricista Certificado", 12,
                        "Electricista certificado SEC. Instalaciones, mantenciones y emergencias en hogares y locales comerciales.",
                        "Tecnico electrico con certificacion SEC clase D y amplia experiencia residencial."},
                {"Daniela", "Morales", "Vega", "daniela.morales@skilllink.cl", "+56 9 6789 0123", premium, lasCondes, 6, "daniela.morales", "Coach de Marketing Digital", 7,
                        "Estratega de marketing digital: campanas, SEO, publicidad pagada y gestion de comunidades.",
                        "Consultora en marketing digital para pymes con resultados medibles."},
        };
        List<Perfil> perfiles = new ArrayList<>();
        for (Object[] p : pros) {
            Persona persona = new Persona();
            persona.setPNombre((String) p[0]);
            persona.setApPaterno((String) p[1]);
            persona.setApMaterno((String) p[2]);
            persona.setEmail((String) p[3]);
            persona.setTelefono((String) p[4]);
            persona.setFechaNacimiento(LocalDate.of(1990, 5, 15));
            persona.setMembresia((Membresia) p[5]);
            personaRepository.save(persona);

            Cliente cliente = new Cliente();
            cliente.setPersona(persona);
            cliente.setMembresia((Membresia) p[5]);
            cliente.getComunas().add((Comuna) p[6]);
            clienteRepository.save(cliente);

            Trabajador trabajador = new Trabajador();
            trabajador.setPersona(persona);
            trabajador.setMembresia((Membresia) p[5]);
            trabajador.getComunas().add((Comuna) p[6]);
            trabajadorRepository.save(trabajador);

            Perfil perfil = new Perfil();
            perfil.setUsername((String) p[8]);
            perfil.setDescripcion((String) p[11]);
            perfil.setCliente(cliente);
            perfil.setTrabajador(trabajador);
            perfil.setCategoriaServicio(categorias.get((Integer) p[7]));
            perfil.setTituloProfesional((String) p[9]);
            perfil.setAniosExperiencia((Integer) p[10]);
            perfilRepository.save(perfil);
            perfiles.add(perfil);

            Curriculum cv = new Curriculum();
            cv.setResumen((String) p[12]);
            cv.setExperienciaJson("{\"anios\":" + p[10] + ",\"area\":\"" + categorias.get((Integer) p[7]).getNombre() + "\"}");
            cv.setTrabajador(trabajador);
            cv.setPerfil(perfil);
            curriculumRepository.save(cv);
        }

        certificacion("Certificacion Personal Trainer NSCA", "NSCA International", LocalDate.of(2023, 3, 15), perfiles.get(0));
        certificacion("AWS Certified Developer", "Amazon Web Services", LocalDate.of(2024, 1, 20), perfiles.get(1));
        certificacion("Certificacion SEC Clase D", "SEC Chile", LocalDate.of(2022, 11, 10), perfiles.get(4));
        certificacion("Google Ads Certification", "Google", LocalDate.of(2024, 6, 5), perfiles.get(5));
        certificacion("Adobe Certified Expert", "Adobe", LocalDate.of(2023, 8, 22), perfiles.get(3));

        // Publicaciones (ofertas de servicio con banda de precios y reputacion)
        Object[][] pubs = {
                {0, "Entrenamiento Personalizado", "Sesiones de entrenamiento adaptadas a tus objetivos, ya sea perdida de peso, ganancia muscular o preparacion deportiva.", 20000, 35000, "por sesion", "1 hora", "4.9", 45},
                {1, "Desarrollo Web Full Stack", "Desarrollo de aplicaciones web completas con Angular, Spring Boot y bases de datos SQL/NoSQL.", 40000, 80000, "por proyecto", "2 a 6 semanas", "5.0", 32},
                {2, "Fotografia de Eventos Sociales", "Cobertura fotografica profesional para matrimonios, cumpleanos y eventos corporativos.", 60000, 150000, "por evento", "4 a 8 horas", "4.8", 67},
                {3, "Diseno de Marca e Identidad Visual", "Creacion completa de identidad de marca: logo, paleta de colores, tipografia y manual de marca.", 80000, 200000, "por proyecto", "3 semanas", "4.9", 28},
                {4, "Instalacion Electrica Residencial", "Instalacion, reparacion y mantencion de sistemas electricos para hogares y departamentos.", 25000, 60000, "por visita", "2 horas", "4.7", 89},
                {5, "Estrategia de Marketing Digital", "Planificacion y ejecucion de campanas en redes sociales, SEO y publicidad pagada.", 35000, 90000, "mensual", "1 mes", "5.0", 41},
                {0, "Plan Nutricional Deportivo", "Planes de alimentacion personalizados para atletas y personas activas.", 15000, 25000, "mensual", "1 mes", "4.8", 33},
                {1, "Desarrollo de Apps Moviles", "Aplicaciones nativas e hibridas para iOS y Android con diseno UX/UI incluido.", 100000, 500000, "por proyecto", "1 a 3 meses", "5.0", 18},
                {4, "Reparacion de Canerias", "Servicio de gasfiteria integral: reparacion de filtraciones, cambio de llaves y destapar canerias.", 20000, 45000, "por visita", "1 a 3 horas", "4.6", 55},
                {2, "Sesion Fotografica Profesional", "Sesiones de retrato, book profesional y fotografia de producto para emprendedores.", 40000, 80000, "por sesion", "2 horas", "4.9", 52},
                {0, "Muebles a Medida", "Diseno y fabricacion de muebles personalizados en madera: repisas, escritorios, closets.", 80000, 300000, "por proyecto", "2 a 4 semanas", "4.7", 19},
                {5, "Community Manager", "Gestion completa de redes sociales: creacion de contenido, programacion y analisis de metricas.", 30000, 60000, "mensual", "1 mes", "4.8", 36},
        };
        for (Object[] p : pubs) {
            Perfil perfil = perfiles.get((Integer) p[0]);
            Publicacion pub = new Publicacion();
            pub.setTipoPublicacion("OFERTA_SERVICIO");
            pub.setTitulo((String) p[1]);
            pub.setDescripcion((String) p[2]);
            pub.setEstado("activo");
            pub.setPerfil(perfil);
            pub.setCurriculum(curriculumRepository.findByPerfilId(perfil.getId()).orElse(null));
            pub.setPrecioMin(BigDecimal.valueOf((Integer) p[3]));
            pub.setPrecioMax(BigDecimal.valueOf((Integer) p[4]));
            pub.setTipoPrecio(BigDecimal.valueOf((Integer) p[3]));
            pub.setModalidadPrecio((String) p[5]);
            pub.setMoneda("CLP");
            pub.setDuracionEstimada((String) p[6]);
            pub.setCalificacionPromedio(new BigDecimal((String) p[7]));
            pub.setTotalResenas((Integer) p[8]);
            publicacionRepository.save(pub);
        }
        log.info("Datos de demostracion cargados: {} categorias, {} profesionales, {} publicaciones.",
                categorias.size(), perfiles.size(), pubs.length);
    }

    private Membresia membresia(String nombre) {
        Membresia m = new Membresia();
        m.setNombre(nombre);
        return membresiaRepository.save(m);
    }

    private Region region(String nombre, Pais pais) {
        Region r = new Region();
        r.setNombre(nombre);
        r.setPais(pais);
        return regionRepository.save(r);
    }

    private Ciudad ciudad(String nombre, Region region) {
        Ciudad c = new Ciudad();
        c.setNombre(nombre);
        c.setRegion(region);
        return ciudadRepository.save(c);
    }

    private Comuna comuna(String nombre, Ciudad ciudad) {
        Comuna c = new Comuna();
        c.setNombre(nombre);
        c.setCiudad(ciudad);
        return comunaRepository.save(c);
    }

    private void certificacion(String nombre, String entidad, LocalDate fecha, Perfil perfil) {
        Certificacion c = new Certificacion();
        c.setNombre(nombre);
        c.setEntidadEmisora(entidad);
        c.setFechaEmision(fecha);
        c.setUrlVerificacion("https://verificacion.skilllink.cl/" + perfil.getUsername());
        c.setPerfil(perfil);
        certificacionRepository.save(c);
        Trabajador t = perfil.getTrabajador();
        t.getCertificaciones().add(c);
        trabajadorRepository.save(t);
    }
}

package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.ConflictoException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.TrabajadorDto;
import SkillLinkBackend.SkillLink.models.entities.Certificacion;
import SkillLinkBackend.SkillLink.models.entities.Comuna;
import SkillLinkBackend.SkillLink.models.entities.Membresia;
import SkillLinkBackend.SkillLink.models.entities.Persona;
import SkillLinkBackend.SkillLink.models.entities.Servicio;
import SkillLinkBackend.SkillLink.models.entities.Trabajador;
import SkillLinkBackend.SkillLink.models.requests.ActualizarTrabajador;
import SkillLinkBackend.SkillLink.models.requests.AgregarTrabajador;
import SkillLinkBackend.SkillLink.repositories.CertificacionRepository;
import SkillLinkBackend.SkillLink.repositories.ComunaRepository;
import SkillLinkBackend.SkillLink.repositories.MembresiaRepository;
import SkillLinkBackend.SkillLink.repositories.PersonaRepository;
import SkillLinkBackend.SkillLink.repositories.ServicioRepository;
import SkillLinkBackend.SkillLink.repositories.TrabajadorRepository;
import SkillLinkBackend.SkillLink.services.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final PersonaRepository personaRepository;
    private final MembresiaRepository membresiaRepository;
    private final CertificacionRepository certificacionRepository;
    private final ServicioRepository servicioRepository;
    private final ComunaRepository comunaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDto> listar() {
        return trabajadorRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajadorDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorDto> obtenerPorPersonaId(Long personaId) {
        return trabajadorRepository.findByPersonaId(personaId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public TrabajadorDto crear(AgregarTrabajador request) {
        Persona persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada con id " + request.getPersonaId()));
        verificarPropiedad(persona);
        if (trabajadorRepository.existsByPersonaIdAndMembresiaId(request.getPersonaId(), request.getMembresiaId())) {
            throw new ConflictoException("La persona ya tiene un rol de trabajador con esa membresia.");
        }
        Trabajador trabajador = new Trabajador();
        aplicarRequest(trabajador, request.getPersonaId(), request.getMembresiaId(),
                request.getCertificacionIds(), request.getServicioIds(), request.getComunaIds());
        return aDto(trabajadorRepository.save(trabajador));
    }

    @Override
    @Transactional
    public TrabajadorDto actualizar(Long id, ActualizarTrabajador request) {
        Trabajador trabajador = obtenerEntidad(id);
        verificarPropiedad(trabajador.getPersona());
        aplicarRequest(trabajador, request.getPersonaId(), request.getMembresiaId(),
                request.getCertificacionIds(), request.getServicioIds(), request.getComunaIds());
        return aDto(trabajadorRepository.save(trabajador));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Trabajador trabajador = obtenerEntidad(id);
        verificarPropiedad(trabajador.getPersona());
        trabajadorRepository.delete(trabajador);
    }

    private void aplicarRequest(Trabajador trabajador, Long personaId, Long membresiaId,
                                List<Long> certificacionIds, List<Long> servicioIds, List<Long> comunaIds) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada con id " + personaId));
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Membresia no encontrada con id " + membresiaId));
        trabajador.setPersona(persona);
        trabajador.setMembresia(membresia);
        trabajador.getCertificaciones().clear();
        for (Long certificacionId : orEmpty(certificacionIds)) {
            Certificacion certificacion = certificacionRepository.findById(certificacionId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Certificacion no encontrada con id " + certificacionId));
            trabajador.getCertificaciones().add(certificacion);
        }
        trabajador.getServicios().clear();
        for (Long servicioId : orEmpty(servicioIds)) {
            Servicio servicio = servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado con id " + servicioId));
            trabajador.getServicios().add(servicio);
        }
        trabajador.getComunas().clear();
        for (Long comunaId : orEmpty(comunaIds)) {
            Comuna comuna = comunaRepository.findById(comunaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Comuna no encontrada con id " + comunaId));
            trabajador.getComunas().add(comuna);
        }
    }

    private void verificarPropiedad(Persona persona) {
        SecurityUtils.emailTokenActual().ifPresent(emailToken -> {
            if (!emailToken.equals(persona.getEmail().toLowerCase())) {
                throw new AccionNoPermitidaException(
                        "El trabajador pertenece a una persona distinta del usuario autenticado.");
            }
        });
    }

    private List<Long> orEmpty(List<Long> ids) {
        return ids == null ? List.of() : ids;
    }

    private Trabajador obtenerEntidad(Long id) {
        return trabajadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajador no encontrado con id " + id));
    }

    private TrabajadorDto aDto(Trabajador trabajador) {
        return new TrabajadorDto(
                trabajador.getId(),
                trabajador.getPersona() != null ? trabajador.getPersona().getId() : null,
                trabajador.getMembresia() != null ? trabajador.getMembresia().getId() : null,
                trabajador.getCertificaciones().stream().map(c -> c.getId()).toList(),
                trabajador.getServicios().stream().map(s -> s.getId()).toList(),
                trabajador.getComunas().stream().map(c -> c.getId()).toList(),
                trabajador.getPerfiles().stream().map(p -> p.getId()).toList());
    }
}
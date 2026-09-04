package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.ConflictoException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.ClienteDto;
import SkillLinkBackend.SkillLink.models.entities.Cliente;
import SkillLinkBackend.SkillLink.models.entities.Comuna;
import SkillLinkBackend.SkillLink.models.entities.Membresia;
import SkillLinkBackend.SkillLink.models.entities.Persona;
import SkillLinkBackend.SkillLink.models.entities.Servicio;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCliente;
import SkillLinkBackend.SkillLink.models.requests.AgregarCliente;
import SkillLinkBackend.SkillLink.repositories.ClienteRepository;
import SkillLinkBackend.SkillLink.repositories.ComunaRepository;
import SkillLinkBackend.SkillLink.repositories.MembresiaRepository;
import SkillLinkBackend.SkillLink.repositories.PersonaRepository;
import SkillLinkBackend.SkillLink.repositories.ServicioRepository;
import SkillLinkBackend.SkillLink.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    private final MembresiaRepository membresiaRepository;
    private final ComunaRepository comunaRepository;
    private final ServicioRepository servicioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDto> listar() {
        return clienteRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDto> obtenerPorPersonaId(Long personaId) {
        return clienteRepository.findByPersonaId(personaId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public ClienteDto crear(AgregarCliente request) {
        Persona persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada con id " + request.getPersonaId()));
        verificarPropiedad(persona);
        if (clienteRepository.existsByPersonaIdAndMembresiaId(request.getPersonaId(), request.getMembresiaId())) {
            throw new ConflictoException("La persona ya tiene un rol de cliente con esa membresia.");
        }
        Cliente cliente = new Cliente();
        aplicarRequest(cliente, request.getPersonaId(), request.getMembresiaId(),
                request.getComunaIds(), request.getServicioIds());
        return aDto(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteDto actualizar(Long id, ActualizarCliente request) {
        Cliente cliente = obtenerEntidad(id);
        verificarPropiedad(cliente.getPersona());
        aplicarRequest(cliente, request.getPersonaId(), request.getMembresiaId(),
                request.getComunaIds(), request.getServicioIds());
        return aDto(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = obtenerEntidad(id);
        verificarPropiedad(cliente.getPersona());
        clienteRepository.delete(cliente);
    }

    private void aplicarRequest(Cliente cliente, Long personaId, Long membresiaId,
                                List<Long> comunaIds, List<Long> servicioIds) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada con id " + personaId));
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Membresia no encontrada con id " + membresiaId));
        cliente.setPersona(persona);
        cliente.setMembresia(membresia);
        cliente.getComunas().clear();
        for (Long comunaId : orEmpty(comunaIds)) {
            Comuna comuna = comunaRepository.findById(comunaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Comuna no encontrada con id " + comunaId));
            cliente.getComunas().add(comuna);
        }
        cliente.getServicios().clear();
        for (Long servicioId : orEmpty(servicioIds)) {
            Servicio servicio = servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado con id " + servicioId));
            cliente.getServicios().add(servicio);
        }
    }

    private void verificarPropiedad(Persona persona) {
        SecurityUtils.emailTokenActual().ifPresent(emailToken -> {
            if (!emailToken.equals(persona.getEmail().toLowerCase())) {
                throw new AccionNoPermitidaException(
                        "El cliente pertenece a una persona distinta del usuario autenticado.");
            }
        });
    }

    private List<Long> orEmpty(List<Long> ids) {
        return ids == null ? List.of() : ids;
    }

    private Cliente obtenerEntidad(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id " + id));
    }

    private ClienteDto aDto(Cliente cliente) {
        return new ClienteDto(
                cliente.getId(),
                cliente.getPersona() != null ? cliente.getPersona().getId() : null,
                cliente.getMembresia() != null ? cliente.getMembresia().getId() : null,
                cliente.getComunas().stream().map(c -> c.getId()).toList(),
                cliente.getServicios().stream().map(s -> s.getId()).toList(),
                cliente.getPerfiles().stream().map(p -> p.getId()).toList());
    }
}
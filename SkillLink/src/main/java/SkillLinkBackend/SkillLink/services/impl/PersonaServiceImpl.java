package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.ConflictoException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.PersonaDto;
import SkillLinkBackend.SkillLink.models.entities.Membresia;
import SkillLinkBackend.SkillLink.models.entities.Persona;
import SkillLinkBackend.SkillLink.models.enums.Rol;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPersona;
import SkillLinkBackend.SkillLink.models.requests.AgregarPersona;
import SkillLinkBackend.SkillLink.repositories.MembresiaRepository;
import SkillLinkBackend.SkillLink.repositories.PersonaRepository;
import SkillLinkBackend.SkillLink.services.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final MembresiaRepository membresiaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDto> listar() {
        return personaRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonaDto> obtenerPorEmail(String email) {
        return personaRepository.findByEmail(email).map(this::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDto obtenerSesionActual() {
        String email = SecurityUtils.emailObligatorio();
        return aDto(personaRepository.findByEmailConRoles(email)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe persona registrada para el usuario autenticado.")));
    }

    @Override
    @Transactional
    public PersonaDto crear(AgregarPersona request) {
        verificarPropiedadEmail(request.getEmail());
        if (personaRepository.existsByEmail(request.getEmail())) {
            throw new ConflictoException("Ya existe una persona con el email " + request.getEmail());
        }
        Persona persona = new Persona();
        aplicarRequest(persona, request.getPNombre(), request.getSNombre(), request.getApPaterno(),
                request.getApMaterno(), request.getEmail(), request.getTelefono(),
                request.getFechaNacimiento(), request.getMembresiaId());
        return aDto(personaRepository.save(persona));
    }

    @Override
    @Transactional
    public PersonaDto actualizar(Long id, ActualizarPersona request) {
        Persona persona = obtenerEntidad(id);
        verificarPropiedadEmail(request.getEmail());
        personaRepository.findByEmail(request.getEmail())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictoException("Ya existe una persona con el email " + request.getEmail());
                });
        aplicarRequest(persona, request.getPNombre(), request.getSNombre(), request.getApPaterno(),
                request.getApMaterno(), request.getEmail(), request.getTelefono(),
                request.getFechaNacimiento(), request.getMembresiaId());
        return aDto(personaRepository.save(persona));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Persona persona = obtenerEntidad(id);
        verificarPropiedadEmail(persona.getEmail());
        personaRepository.delete(persona);
    }

    private void verificarPropiedadEmail(String email) {
        SecurityUtils.emailTokenActual().ifPresent(emailToken -> {
            if (!emailToken.equals(email.toLowerCase())) {
                throw new AccionNoPermitidaException(
                        "El email de la persona debe coincidir con el del usuario autenticado.");
            }
        });
    }

    private void aplicarRequest(Persona persona, String pNombre, String sNombre, String apPaterno,
                                String apMaterno, String email, String telefono, java.time.LocalDate fechaNacimiento,
                                Long membresiaId) {
        persona.setPNombre(pNombre);
        persona.setSNombre(sNombre);
        persona.setApPaterno(apPaterno);
        persona.setApMaterno(apMaterno);
        persona.setEmail(email);
        persona.setTelefono(telefono);
        persona.setFechaNacimiento(fechaNacimiento);
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Membresia no encontrada con id " + membresiaId));
        persona.setMembresia(membresia);
    }

    private Persona obtenerEntidad(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada con id " + id));
    }

    private PersonaDto aDto(Persona persona) {
        List<Long> clienteIds = persona.getClientes().stream().map(c -> c.getId()).toList();
        List<Long> trabajadorIds = persona.getTrabajadores().stream().map(t -> t.getId()).toList();
        List<Rol> roles = new ArrayList<>();
        if (!clienteIds.isEmpty()) {
            roles.add(Rol.CLIENTE);
        }
        if (!trabajadorIds.isEmpty()) {
            roles.add(Rol.TRABAJADOR);
        }
        return new PersonaDto(
                persona.getId(),
                persona.getPNombre(),
                persona.getSNombre(),
                persona.getApPaterno(),
                persona.getApMaterno(),
                persona.getEmail(),
                persona.getTelefono(),
                persona.getFechaNacimiento(),
                persona.getMembresia() != null ? persona.getMembresia().getId() : null,
                clienteIds,
                trabajadorIds,
                roles);
    }
}
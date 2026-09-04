package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.PersonaDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPersona;
import SkillLinkBackend.SkillLink.models.requests.AgregarPersona;

import java.util.List;
import java.util.Optional;

public interface PersonaService {

    List<PersonaDto> listar();

    PersonaDto obtenerPorId(Long id);

    Optional<PersonaDto> obtenerPorEmail(String email);

    PersonaDto obtenerSesionActual();

    PersonaDto crear(AgregarPersona request);

    PersonaDto actualizar(Long id, ActualizarPersona request);

    void eliminar(Long id);
}
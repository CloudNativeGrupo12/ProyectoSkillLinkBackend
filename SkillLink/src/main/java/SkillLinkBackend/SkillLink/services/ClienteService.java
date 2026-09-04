package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.ClienteDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCliente;
import SkillLinkBackend.SkillLink.models.requests.AgregarCliente;

import java.util.List;

public interface ClienteService {

    List<ClienteDto> listar();

    ClienteDto obtenerPorId(Long id);

    List<ClienteDto> obtenerPorPersonaId(Long personaId);

    ClienteDto crear(AgregarCliente request);

    ClienteDto actualizar(Long id, ActualizarCliente request);

    void eliminar(Long id);
}
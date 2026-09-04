package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.PerfilDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPerfil;
import SkillLinkBackend.SkillLink.models.requests.AgregarPerfil;

import java.util.List;
import java.util.Optional;

public interface PerfilService {

    List<PerfilDto> listar();

    PerfilDto obtenerPorId(Long id);

    Optional<PerfilDto> obtenerPorUsername(String username);

    Optional<PerfilDto> obtenerPorTrabajadorId(Long trabajadorId);

    PerfilDto crear(AgregarPerfil request);

    PerfilDto actualizar(Long id, ActualizarPerfil request);

    void eliminar(Long id);
}
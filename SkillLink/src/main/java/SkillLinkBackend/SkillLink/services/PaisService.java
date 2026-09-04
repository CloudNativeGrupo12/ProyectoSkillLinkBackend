package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.PaisDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPais;
import SkillLinkBackend.SkillLink.models.requests.AgregarPais;

import java.util.List;

public interface PaisService {

    List<PaisDto> listar();

    PaisDto obtenerPorId(Long id);

    PaisDto crear(AgregarPais request);

    PaisDto actualizar(Long id, ActualizarPais request);

    void eliminar(Long id);
}
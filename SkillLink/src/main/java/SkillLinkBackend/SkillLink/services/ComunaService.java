package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.ComunaDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarComuna;
import SkillLinkBackend.SkillLink.models.requests.AgregarComuna;

import java.util.List;

public interface ComunaService {

    List<ComunaDto> listar();

    ComunaDto obtenerPorId(Long id);

    List<ComunaDto> obtenerPorCiudadId(Long ciudadId);

    ComunaDto crear(AgregarComuna request);

    ComunaDto actualizar(Long id, ActualizarComuna request);

    void eliminar(Long id);
}
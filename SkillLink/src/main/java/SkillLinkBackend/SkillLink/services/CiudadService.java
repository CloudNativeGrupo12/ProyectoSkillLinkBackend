package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.CiudadDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCiudad;
import SkillLinkBackend.SkillLink.models.requests.AgregarCiudad;

import java.util.List;

public interface CiudadService {

    List<CiudadDto> listar();

    CiudadDto obtenerPorId(Long id);

    List<CiudadDto> obtenerPorRegionId(Long regionId);

    CiudadDto crear(AgregarCiudad request);

    CiudadDto actualizar(Long id, ActualizarCiudad request);

    void eliminar(Long id);
}
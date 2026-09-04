package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.RegionDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarRegion;
import SkillLinkBackend.SkillLink.models.requests.AgregarRegion;

import java.util.List;

public interface RegionService {

    List<RegionDto> listar();

    RegionDto obtenerPorId(Long id);

    List<RegionDto> obtenerPorPaisId(Long paisId);

    RegionDto crear(AgregarRegion request);

    RegionDto actualizar(Long id, ActualizarRegion request);

    void eliminar(Long id);
}
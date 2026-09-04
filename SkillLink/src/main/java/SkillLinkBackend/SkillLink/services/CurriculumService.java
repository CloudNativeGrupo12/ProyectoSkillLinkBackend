package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.CurriculumDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCurriculum;
import SkillLinkBackend.SkillLink.models.requests.AgregarCurriculum;

import java.util.List;
import java.util.Optional;

public interface CurriculumService {

    List<CurriculumDto> listar();

    CurriculumDto obtenerPorId(Long id);

    List<CurriculumDto> obtenerPorTrabajadorId(Long trabajadorId);

    Optional<CurriculumDto> obtenerPorPerfilId(Long perfilId);

    CurriculumDto crear(AgregarCurriculum request);

    CurriculumDto actualizar(Long id, ActualizarCurriculum request);

    void eliminar(Long id);
}
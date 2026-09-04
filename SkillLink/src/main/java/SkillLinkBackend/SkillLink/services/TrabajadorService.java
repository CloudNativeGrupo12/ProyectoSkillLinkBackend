package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.TrabajadorDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarTrabajador;
import SkillLinkBackend.SkillLink.models.requests.AgregarTrabajador;

import java.util.List;

public interface TrabajadorService {

    List<TrabajadorDto> listar();

    TrabajadorDto obtenerPorId(Long id);

    List<TrabajadorDto> obtenerPorPersonaId(Long personaId);

    TrabajadorDto crear(AgregarTrabajador request);

    TrabajadorDto actualizar(Long id, ActualizarTrabajador request);

    void eliminar(Long id);
}
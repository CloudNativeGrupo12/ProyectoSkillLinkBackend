package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.CategoriaServicioDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCategoriaServicio;
import SkillLinkBackend.SkillLink.models.requests.AgregarCategoriaServicio;

import java.util.List;

public interface CategoriaServicioService {

    List<CategoriaServicioDto> listar();

    CategoriaServicioDto obtenerPorId(Long id);

    CategoriaServicioDto crear(AgregarCategoriaServicio request);

    CategoriaServicioDto actualizar(Long id, ActualizarCategoriaServicio request);

    void eliminar(Long id);
}
package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.ServicioDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarServicio;
import SkillLinkBackend.SkillLink.models.requests.AgregarServicio;

import java.util.List;

public interface ServicioService {

    List<ServicioDto> listar();

    List<ServicioDto> listarActivos();

    List<ServicioDto> buscarPorNombre(String nombre);

    ServicioDto obtenerPorId(Long id);

    ServicioDto crear(AgregarServicio request);

    ServicioDto actualizar(Long id, ActualizarServicio request);

    void eliminar(Long id);
}
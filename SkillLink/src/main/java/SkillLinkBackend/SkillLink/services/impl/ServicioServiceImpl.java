package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.ServicioDto;
import SkillLinkBackend.SkillLink.models.entities.CategoriaServicio;
import SkillLinkBackend.SkillLink.models.entities.Servicio;
import SkillLinkBackend.SkillLink.models.requests.ActualizarServicio;
import SkillLinkBackend.SkillLink.models.requests.AgregarServicio;
import SkillLinkBackend.SkillLink.repositories.CategoriaServicioRepository;
import SkillLinkBackend.SkillLink.repositories.ServicioRepository;
import SkillLinkBackend.SkillLink.services.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final CategoriaServicioRepository categoriaServicioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ServicioDto> listar() {
        return servicioRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioDto> listarActivos() {
        return servicioRepository.findByIsActivoTrue().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioDto> buscarPorNombre(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public ServicioDto crear(AgregarServicio request) {
        Servicio servicio = new Servicio();
        aplicarRequest(servicio, request.getNombre(), request.getDescripcion(), request.isActivo(),
                request.getCategoriaIds());
        return aDto(servicioRepository.save(servicio));
    }

    @Override
    @Transactional
    public ServicioDto actualizar(Long id, ActualizarServicio request) {
        Servicio servicio = obtenerEntidad(id);
        aplicarRequest(servicio, request.getNombre(), request.getDescripcion(), request.isActivo(),
                request.getCategoriaIds());
        return aDto(servicioRepository.save(servicio));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        servicioRepository.delete(obtenerEntidad(id));
    }

    private void aplicarRequest(Servicio servicio, String nombre, String descripcion, boolean isActivo,
                                List<Long> categoriaIds) {
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);
        servicio.setActivo(isActivo);
        servicio.getCategorias().clear();
        for (Long categoriaId : orEmpty(categoriaIds)) {
            CategoriaServicio categoria = categoriaServicioRepository.findById(categoriaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada con id " + categoriaId));
            servicio.getCategorias().add(categoria);
        }
    }

    private List<Long> orEmpty(List<Long> ids) {
        return ids == null ? List.of() : ids;
    }

    private Servicio obtenerEntidad(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado con id " + id));
    }

    private ServicioDto aDto(Servicio servicio) {
        return new ServicioDto(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.isActivo(),
                servicio.getCategorias().stream().map(c -> c.getId()).toList());
    }
}
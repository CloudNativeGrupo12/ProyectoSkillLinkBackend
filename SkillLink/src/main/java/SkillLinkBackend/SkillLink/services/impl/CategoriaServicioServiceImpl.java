package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.CategoriaServicioDto;
import SkillLinkBackend.SkillLink.models.entities.CategoriaServicio;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCategoriaServicio;
import SkillLinkBackend.SkillLink.models.requests.AgregarCategoriaServicio;
import SkillLinkBackend.SkillLink.repositories.CategoriaServicioRepository;
import SkillLinkBackend.SkillLink.services.CategoriaServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServicioServiceImpl implements CategoriaServicioService {

    private final CategoriaServicioRepository categoriaServicioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaServicioDto> listar() {
        return categoriaServicioRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaServicioDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public CategoriaServicioDto crear(AgregarCategoriaServicio request) {
        CategoriaServicio categoria = new CategoriaServicio();
        categoria.setNombre(request.getNombre());
        return aDto(categoriaServicioRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaServicioDto actualizar(Long id, ActualizarCategoriaServicio request) {
        CategoriaServicio categoria = obtenerEntidad(id);
        categoria.setNombre(request.getNombre());
        return aDto(categoriaServicioRepository.save(categoria));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        categoriaServicioRepository.delete(obtenerEntidad(id));
    }

    private CategoriaServicio obtenerEntidad(Long id) {
        return categoriaServicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria de servicio no encontrada con id " + id));
    }

    private CategoriaServicioDto aDto(CategoriaServicio categoria) {
        return new CategoriaServicioDto(categoria.getId(), categoria.getNombre());
    }
}
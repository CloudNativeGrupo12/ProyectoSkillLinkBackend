package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.ComunaDto;
import SkillLinkBackend.SkillLink.models.entities.Ciudad;
import SkillLinkBackend.SkillLink.models.entities.Comuna;
import SkillLinkBackend.SkillLink.models.requests.ActualizarComuna;
import SkillLinkBackend.SkillLink.models.requests.AgregarComuna;
import SkillLinkBackend.SkillLink.repositories.CiudadRepository;
import SkillLinkBackend.SkillLink.repositories.ComunaRepository;
import SkillLinkBackend.SkillLink.services.ComunaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComunaServiceImpl implements ComunaService {

    private final ComunaRepository comunaRepository;
    private final CiudadRepository ciudadRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ComunaDto> listar() {
        return comunaRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComunaDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComunaDto> obtenerPorCiudadId(Long ciudadId) {
        return comunaRepository.findByCiudadId(ciudadId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public ComunaDto crear(AgregarComuna request) {
        Comuna comuna = new Comuna();
        aplicarRequest(comuna, request.getNombre(), request.getCiudadId());
        return aDto(comunaRepository.save(comuna));
    }

    @Override
    @Transactional
    public ComunaDto actualizar(Long id, ActualizarComuna request) {
        Comuna comuna = obtenerEntidad(id);
        aplicarRequest(comuna, request.getNombre(), request.getCiudadId());
        return aDto(comunaRepository.save(comuna));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        comunaRepository.delete(obtenerEntidad(id));
    }

    private void aplicarRequest(Comuna comuna, String nombre, Long ciudadId) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudad no encontrada con id " + ciudadId));
        comuna.setNombre(nombre);
        comuna.setCiudad(ciudad);
    }

    private Comuna obtenerEntidad(Long id) {
        return comunaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comuna no encontrada con id " + id));
    }

    private ComunaDto aDto(Comuna comuna) {
        return new ComunaDto(comuna.getId(), comuna.getNombre(),
                comuna.getCiudad() != null ? comuna.getCiudad().getId() : null);
    }
}
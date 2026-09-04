package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.CiudadDto;
import SkillLinkBackend.SkillLink.models.entities.Ciudad;
import SkillLinkBackend.SkillLink.models.entities.Region;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCiudad;
import SkillLinkBackend.SkillLink.models.requests.AgregarCiudad;
import SkillLinkBackend.SkillLink.repositories.CiudadRepository;
import SkillLinkBackend.SkillLink.repositories.RegionRepository;
import SkillLinkBackend.SkillLink.services.CiudadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CiudadServiceImpl implements CiudadService {

    private final CiudadRepository ciudadRepository;
    private final RegionRepository regionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CiudadDto> listar() {
        return ciudadRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CiudadDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CiudadDto> obtenerPorRegionId(Long regionId) {
        return ciudadRepository.findByRegionId(regionId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public CiudadDto crear(AgregarCiudad request) {
        Ciudad ciudad = new Ciudad();
        aplicarRequest(ciudad, request.getNombre(), request.getRegionId());
        return aDto(ciudadRepository.save(ciudad));
    }

    @Override
    @Transactional
    public CiudadDto actualizar(Long id, ActualizarCiudad request) {
        Ciudad ciudad = obtenerEntidad(id);
        aplicarRequest(ciudad, request.getNombre(), request.getRegionId());
        return aDto(ciudadRepository.save(ciudad));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ciudadRepository.delete(obtenerEntidad(id));
    }

    private void aplicarRequest(Ciudad ciudad, String nombre, Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Region no encontrada con id " + regionId));
        ciudad.setNombre(nombre);
        ciudad.setRegion(region);
    }

    private Ciudad obtenerEntidad(Long id) {
        return ciudadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudad no encontrada con id " + id));
    }

    private CiudadDto aDto(Ciudad ciudad) {
        return new CiudadDto(ciudad.getId(), ciudad.getNombre(),
                ciudad.getRegion() != null ? ciudad.getRegion().getId() : null);
    }
}
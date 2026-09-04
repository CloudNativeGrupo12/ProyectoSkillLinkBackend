package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.RegionDto;
import SkillLinkBackend.SkillLink.models.entities.Pais;
import SkillLinkBackend.SkillLink.models.entities.Region;
import SkillLinkBackend.SkillLink.models.requests.ActualizarRegion;
import SkillLinkBackend.SkillLink.models.requests.AgregarRegion;
import SkillLinkBackend.SkillLink.repositories.PaisRepository;
import SkillLinkBackend.SkillLink.repositories.RegionRepository;
import SkillLinkBackend.SkillLink.services.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;
    private final PaisRepository paisRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RegionDto> listar() {
        return regionRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegionDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionDto> obtenerPorPaisId(Long paisId) {
        return regionRepository.findByPaisId(paisId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public RegionDto crear(AgregarRegion request) {
        Region region = new Region();
        aplicarRequest(region, request.getNombre(), request.getPaisId());
        return aDto(regionRepository.save(region));
    }

    @Override
    @Transactional
    public RegionDto actualizar(Long id, ActualizarRegion request) {
        Region region = obtenerEntidad(id);
        aplicarRequest(region, request.getNombre(), request.getPaisId());
        return aDto(regionRepository.save(region));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        regionRepository.delete(obtenerEntidad(id));
    }

    private void aplicarRequest(Region region, String nombre, Long paisId) {
        Pais pais = paisRepository.findById(paisId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pais no encontrado con id " + paisId));
        region.setNombre(nombre);
        region.setPais(pais);
    }

    private Region obtenerEntidad(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Region no encontrada con id " + id));
    }

    private RegionDto aDto(Region region) {
        return new RegionDto(region.getId(), region.getNombre(),
                region.getPais() != null ? region.getPais().getId() : null);
    }
}
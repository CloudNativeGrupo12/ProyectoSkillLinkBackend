package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.PaisDto;
import SkillLinkBackend.SkillLink.models.entities.Pais;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPais;
import SkillLinkBackend.SkillLink.models.requests.AgregarPais;
import SkillLinkBackend.SkillLink.repositories.PaisRepository;
import SkillLinkBackend.SkillLink.services.PaisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaisServiceImpl implements PaisService {

    private final PaisRepository paisRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaisDto> listar() {
        return paisRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaisDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public PaisDto crear(AgregarPais request) {
        Pais pais = new Pais();
        pais.setNombre(request.getNombre());
        return aDto(paisRepository.save(pais));
    }

    @Override
    @Transactional
    public PaisDto actualizar(Long id, ActualizarPais request) {
        Pais pais = obtenerEntidad(id);
        pais.setNombre(request.getNombre());
        return aDto(paisRepository.save(pais));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        paisRepository.delete(obtenerEntidad(id));
    }

    private Pais obtenerEntidad(Long id) {
        return paisRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pais no encontrado con id " + id));
    }

    private PaisDto aDto(Pais pais) {
        return new PaisDto(pais.getId(), pais.getNombre());
    }
}
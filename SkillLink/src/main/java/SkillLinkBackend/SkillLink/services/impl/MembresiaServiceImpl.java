package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.MembresiaDto;
import SkillLinkBackend.SkillLink.models.entities.Membresia;
import SkillLinkBackend.SkillLink.models.requests.ActualizarMembresia;
import SkillLinkBackend.SkillLink.models.requests.AgregarMembresia;
import SkillLinkBackend.SkillLink.repositories.MembresiaRepository;
import SkillLinkBackend.SkillLink.services.MembresiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembresiaServiceImpl implements MembresiaService {

    private final MembresiaRepository membresiaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MembresiaDto> listar() {
        return membresiaRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MembresiaDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public MembresiaDto crear(AgregarMembresia request) {
        Membresia membresia = new Membresia();
        membresia.setNombre(request.getNombre());
        return aDto(membresiaRepository.save(membresia));
    }

    @Override
    @Transactional
    public MembresiaDto actualizar(Long id, ActualizarMembresia request) {
        Membresia membresia = obtenerEntidad(id);
        membresia.setNombre(request.getNombre());
        return aDto(membresiaRepository.save(membresia));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Membresia membresia = obtenerEntidad(id);
        membresiaRepository.delete(membresia);
    }

    private Membresia obtenerEntidad(Long id) {
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Membresia no encontrada con id " + id));
    }

    private MembresiaDto aDto(Membresia membresia) {
        return new MembresiaDto(membresia.getId(), membresia.getNombre());
    }
}
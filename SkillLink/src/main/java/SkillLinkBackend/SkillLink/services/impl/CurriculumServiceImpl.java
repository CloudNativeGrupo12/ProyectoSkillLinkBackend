package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.ConflictoException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.exceptions.SolicitudInvalidaException;
import SkillLinkBackend.SkillLink.models.dto.CurriculumDto;
import SkillLinkBackend.SkillLink.models.entities.Curriculum;
import SkillLinkBackend.SkillLink.models.entities.Perfil;
import SkillLinkBackend.SkillLink.models.entities.Trabajador;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCurriculum;
import SkillLinkBackend.SkillLink.models.requests.AgregarCurriculum;
import SkillLinkBackend.SkillLink.repositories.CurriculumRepository;
import SkillLinkBackend.SkillLink.repositories.PerfilRepository;
import SkillLinkBackend.SkillLink.repositories.TrabajadorRepository;
import SkillLinkBackend.SkillLink.services.CurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final PerfilRepository perfilRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumDto> listar() {
        return curriculumRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumDto> obtenerPorTrabajadorId(Long trabajadorId) {
        return curriculumRepository.findByTrabajadorId(trabajadorId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurriculumDto> obtenerPorPerfilId(Long perfilId) {
        return curriculumRepository.findByPerfilId(perfilId).map(this::aDto);
    }

    @Override
    @Transactional
    public CurriculumDto crear(AgregarCurriculum request) {
        validarVacioPerfil(request.getPerfilId());
        Trabajador trabajador = trabajadorRepository.findById(request.getTrabajadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajador no encontrado con id " + request.getTrabajadorId()));
        Perfil perfil = perfilRepository.findById(request.getPerfilId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + request.getPerfilId()));
        validarCoherencia(perfil, request.getTrabajadorId());
        verificarPropiedad(perfil);
        Curriculum curriculum = new Curriculum();
        curriculum.setResumen(request.getResumen());
        curriculum.setExperienciaJson(request.getExperienciaJson());
        curriculum.setTrabajador(trabajador);
        curriculum.setPerfil(perfil);
        return aDto(curriculumRepository.save(curriculum));
    }

    @Override
    @Transactional
    public CurriculumDto actualizar(Long id, ActualizarCurriculum request) {
        Curriculum curriculum = obtenerEntidad(id);
        verificarPropiedad(curriculum.getPerfil());
        Long perfilId = request.getPerfilId();
        Curriculum existenteParaPerfil = perfilId != null ? curriculumRepository.findByPerfilId(perfilId).orElse(null) : null;
        if (existenteParaPerfil != null && !existenteParaPerfil.getId().equals(id)) {
            throw new ConflictoException("El perfil ya tiene un curriculum asociado.");
        }
        Trabajador trabajador = trabajadorRepository.findById(request.getTrabajadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajador no encontrado con id " + request.getTrabajadorId()));
        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + perfilId));
        validarCoherencia(perfil, request.getTrabajadorId());
        curriculum.setResumen(request.getResumen());
        curriculum.setExperienciaJson(request.getExperienciaJson());
        curriculum.setTrabajador(trabajador);
        curriculum.setPerfil(perfil);
        return aDto(curriculumRepository.save(curriculum));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Curriculum curriculum = obtenerEntidad(id);
        verificarPropiedad(curriculum.getPerfil());
        curriculumRepository.delete(curriculum);
    }

    private void validarVacioPerfil(Long perfilId) {
        if (curriculumRepository.existsByPerfilId(perfilId)) {
            throw new ConflictoException("El perfil ya tiene un curriculum asociado.");
        }
    }

    private void validarCoherencia(Perfil perfil, Long trabajadorId) {
        if (perfil.getTrabajador() == null || !perfil.getTrabajador().getId().equals(trabajadorId)) {
            throw new SolicitudInvalidaException(
                    "El curriculum debe asociarse al trabajador dueno del perfil.");
        }
    }

    private void verificarPropiedad(Perfil perfil) {
        if (perfil.getTrabajador() == null) {
            throw new AccionNoPermitidaException("El curriculum requiere un perfil con trabajador asociado.");
        }
        String emailToken = SecurityUtils.emailObligatorio();
        if (!emailToken.equals(perfil.getTrabajador().getPersona().getEmail().toLowerCase())) {
            throw new AccionNoPermitidaException(
                    "El perfil pertenece a un trabajador distinto del usuario autenticado.");
        }
    }

    private Curriculum obtenerEntidad(Long id) {
        return curriculumRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curriculum no encontrado con id " + id));
    }

    private CurriculumDto aDto(Curriculum curriculum) {
        return new CurriculumDto(
                curriculum.getId(),
                curriculum.getResumen(),
                curriculum.getExperienciaJson(),
                curriculum.getTrabajador() != null ? curriculum.getTrabajador().getId() : null,
                curriculum.getPerfil() != null ? curriculum.getPerfil().getId() : null);
    }
}
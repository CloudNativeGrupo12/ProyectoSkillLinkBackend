package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.exceptions.SolicitudInvalidaException;
import SkillLinkBackend.SkillLink.models.dto.PublicacionDto;
import SkillLinkBackend.SkillLink.models.entities.Curriculum;
import SkillLinkBackend.SkillLink.models.entities.Perfil;
import SkillLinkBackend.SkillLink.models.entities.Publicacion;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPublicacion;
import SkillLinkBackend.SkillLink.models.requests.AgregarPublicacion;
import SkillLinkBackend.SkillLink.repositories.CurriculumRepository;
import SkillLinkBackend.SkillLink.repositories.PerfilRepository;
import SkillLinkBackend.SkillLink.repositories.PublicacionRepository;
import SkillLinkBackend.SkillLink.services.PublicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicacionServiceImpl implements PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final PerfilRepository perfilRepository;
    private final CurriculumRepository curriculumRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionDto> listar() {
        return publicacionRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PublicacionDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionDto> obtenerPorPerfilId(Long perfilId) {
        return publicacionRepository.findByPerfilId(perfilId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionDto> buscar(Long categoriaId, Long comunaId, BigDecimal precioMin,
                                       BigDecimal precioMax, String termino) {
        String terminoNormalizado = (termino == null || termino.isBlank()) ? null : termino.trim();
        return publicacionRepository.buscar(categoriaId, comunaId, precioMin, precioMax, terminoNormalizado).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public PublicacionDto crear(AgregarPublicacion request) {
        Perfil perfil = perfilRepository.findById(request.getPerfilId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + request.getPerfilId()));
        verificarPropiedad(perfil);
        Publicacion publicacion = new Publicacion();
        publicacion.setTipoPublicacion(request.getTipoPublicacion());
        publicacion.setPerfil(perfil);
        publicacion.setCurriculum(validarCurriculum(request.getCurriculumId(), request.getPerfilId()));
        publicacion.setPrecioMin(request.getPrecioMin());
        publicacion.setPrecioMax(request.getPrecioMax());
        publicacion.setTipoPrecio(request.getTipoPrecio());
        publicacion.setMoneda(request.getMoneda());
        publicacion.setDuracionEstimada(request.getDuracionEstimada());
        return aDto(publicacionRepository.save(publicacion));
    }

    @Override
    @Transactional
    public PublicacionDto actualizar(Long id, ActualizarPublicacion request) {
        Publicacion publicacion = obtenerEntidad(id);
        verificarPropiedad(publicacion.getPerfil());
        Perfil perfil = publicacion.getPerfil();
        if (!perfil.getId().equals(request.getPerfilId())) {
            perfil = perfilRepository.findById(request.getPerfilId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + request.getPerfilId()));
            verificarPropiedad(perfil);
        }
        publicacion.setTipoPublicacion(request.getTipoPublicacion());
        publicacion.setPerfil(perfil);
        publicacion.setCurriculum(validarCurriculum(request.getCurriculumId(), request.getPerfilId()));
        publicacion.setPrecioMin(request.getPrecioMin());
        publicacion.setPrecioMax(request.getPrecioMax());
        publicacion.setTipoPrecio(request.getTipoPrecio());
        publicacion.setMoneda(request.getMoneda());
        publicacion.setDuracionEstimada(request.getDuracionEstimada());
        return aDto(publicacionRepository.save(publicacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Publicacion publicacion = obtenerEntidad(id);
        verificarPropiedad(publicacion.getPerfil());
        publicacionRepository.delete(publicacion);
    }

    private void verificarPropiedad(Perfil perfil) {
        if (perfil.getTrabajador() == null) {
            throw new AccionNoPermitidaException("La publicacion requiere un perfil con trabajador asociado.");
        }
        String emailToken = SecurityUtils.emailObligatorio();
        if (!emailToken.equals(perfil.getTrabajador().getPersona().getEmail().toLowerCase())) {
            throw new AccionNoPermitidaException(
                    "El perfil pertenece a un trabajador distinto del usuario autenticado.");
        }
    }

    private Curriculum validarCurriculum(Long curriculumId, Long perfilId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curriculum no encontrado con id " + curriculumId));
        if (curriculum.getPerfil() == null || !curriculum.getPerfil().getId().equals(perfilId)) {
            throw new SolicitudInvalidaException("El curriculum debe pertenecer al perfil de la publicacion.");
        }
        return curriculum;
    }

    private Publicacion obtenerEntidad(Long id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicacion no encontrada con id " + id));
    }

    private PublicacionDto aDto(Publicacion publicacion) {
        return new PublicacionDto(
                publicacion.getId(),
                publicacion.getTipoPublicacion(),
                publicacion.getCreadoEn(),
                publicacion.getActualizadoEn(),
                publicacion.getPerfil() != null ? publicacion.getPerfil().getId() : null,
                publicacion.getCurriculum() != null ? publicacion.getCurriculum().getId() : null,
                publicacion.getPrecioMin(),
                publicacion.getPrecioMax(),
                publicacion.getTipoPrecio(),
                publicacion.getMoneda(),
                publicacion.getDuracionEstimada());
    }
}
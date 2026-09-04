package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.models.dto.CertificacionDto;
import SkillLinkBackend.SkillLink.models.entities.Certificacion;
import SkillLinkBackend.SkillLink.models.entities.Perfil;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCertificacion;
import SkillLinkBackend.SkillLink.models.requests.AgregarCertificacion;
import SkillLinkBackend.SkillLink.repositories.CertificacionRepository;
import SkillLinkBackend.SkillLink.repositories.PerfilRepository;
import SkillLinkBackend.SkillLink.services.CertificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificacionServiceImpl implements CertificacionService {

    private final CertificacionRepository certificacionRepository;
    private final PerfilRepository perfilRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionDto> listar() {
        return certificacionRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CertificacionDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificacionDto> obtenerPorPerfilId(Long perfilId) {
        return certificacionRepository.findByPerfilId(perfilId).stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional
    public CertificacionDto crear(AgregarCertificacion request) {
        Perfil perfil = perfilRepository.findById(request.getPerfilId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + request.getPerfilId()));
        verificarPropiedad(perfil);
        Certificacion certificacion = new Certificacion();
        certificacion.setNombre(request.getNombre());
        certificacion.setEntidadEmisora(request.getEntidadEmisora());
        certificacion.setFechaEmision(request.getFechaEmision());
        certificacion.setUrlVerificacion(request.getUrlVerificacion());
        certificacion.setPerfil(perfil);
        return aDto(certificacionRepository.save(certificacion));
    }

    @Override
    @Transactional
    public CertificacionDto actualizar(Long id, ActualizarCertificacion request) {
        Certificacion certificacion = obtenerEntidad(id);
        verificarPropiedad(certificacion.getPerfil());
        Perfil perfil = certificacion.getPerfil();
        if (!perfil.getId().equals(request.getPerfilId())) {
            perfil = perfilRepository.findById(request.getPerfilId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + request.getPerfilId()));
            verificarPropiedad(perfil);
        }
        certificacion.setNombre(request.getNombre());
        certificacion.setEntidadEmisora(request.getEntidadEmisora());
        certificacion.setFechaEmision(request.getFechaEmision());
        certificacion.setUrlVerificacion(request.getUrlVerificacion());
        certificacion.setPerfil(perfil);
        return aDto(certificacionRepository.save(certificacion));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Certificacion certificacion = obtenerEntidad(id);
        verificarPropiedad(certificacion.getPerfil());
        certificacionRepository.delete(certificacion);
    }

    private void verificarPropiedad(Perfil perfil) {
        if (perfil.getTrabajador() == null) {
            throw new AccionNoPermitidaException("La certificacion requiere un perfil con trabajador asociado.");
        }
        String emailToken = SecurityUtils.emailObligatorio();
        if (!emailToken.equals(perfil.getTrabajador().getPersona().getEmail().toLowerCase())) {
            throw new AccionNoPermitidaException(
                    "El perfil pertenece a un trabajador distinto del usuario autenticado.");
        }
    }

    private Certificacion obtenerEntidad(Long id) {
        return certificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Certificacion no encontrada con id " + id));
    }

    private CertificacionDto aDto(Certificacion certificacion) {
        return new CertificacionDto(
                certificacion.getId(),
                certificacion.getNombre(),
                certificacion.getEntidadEmisora(),
                certificacion.getFechaEmision(),
                certificacion.getUrlVerificacion(),
                certificacion.getPerfil() != null ? certificacion.getPerfil().getId() : null);
    }
}
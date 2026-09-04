package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.CertificacionDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCertificacion;
import SkillLinkBackend.SkillLink.models.requests.AgregarCertificacion;

import java.util.List;

public interface CertificacionService {

    List<CertificacionDto> listar();

    CertificacionDto obtenerPorId(Long id);

    List<CertificacionDto> obtenerPorPerfilId(Long perfilId);

    CertificacionDto crear(AgregarCertificacion request);

    CertificacionDto actualizar(Long id, ActualizarCertificacion request);

    void eliminar(Long id);
}
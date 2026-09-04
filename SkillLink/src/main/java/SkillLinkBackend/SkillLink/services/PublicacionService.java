package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.PublicacionDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPublicacion;
import SkillLinkBackend.SkillLink.models.requests.AgregarPublicacion;

import java.math.BigDecimal;
import java.util.List;

public interface PublicacionService {

    List<PublicacionDto> listar();

    PublicacionDto obtenerPorId(Long id);

    List<PublicacionDto> obtenerPorPerfilId(Long perfilId);

    List<PublicacionDto> buscar(Long categoriaId, Long comunaId, BigDecimal precioMin, BigDecimal precioMax, String termino);

    PublicacionDto crear(AgregarPublicacion request);

    PublicacionDto actualizar(Long id, ActualizarPublicacion request);

    void eliminar(Long id);
}
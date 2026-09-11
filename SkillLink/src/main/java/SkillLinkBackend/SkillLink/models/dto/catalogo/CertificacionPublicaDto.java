package SkillLinkBackend.SkillLink.models.dto.catalogo;

import java.time.LocalDate;

public record CertificacionPublicaDto(Long id, String nombre, String entidad, LocalDate fecha,
                                      String urlVerificacion, Long trabajadorId) {
}

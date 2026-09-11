package SkillLinkBackend.SkillLink.models.dto.catalogo;

import java.math.BigDecimal;

/** Vista publica resumida de un trabajador (sin datos de contacto). */
public record ProfesionalCatalogoDto(
        Long id,
        Long perfilId,
        String username,
        String nombre,
        String apPaterno,
        String rol,
        String ciudad,
        String comuna,
        BigDecimal rating,
        int reviews,
        BigDecimal precio,
        Long membresiaId,
        boolean verificado,
        Long categoriaId) {
}

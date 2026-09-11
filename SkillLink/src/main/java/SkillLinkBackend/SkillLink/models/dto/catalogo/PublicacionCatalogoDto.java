package SkillLinkBackend.SkillLink.models.dto.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Publicacion (oferta de servicio) enriquecida con categoria, trabajador y ubicacion. */
public record PublicacionCatalogoDto(
        Long id,
        String nombre,
        String descripcion,
        Long categoriaId,
        String categoriaNombre,
        Long trabajadorId,
        Long perfilId,
        String trabajadorNombre,
        String ubicacion,
        BigDecimal precioMin,
        BigDecimal precioMax,
        String tipoPrecio,
        String moneda,
        String estado,
        BigDecimal rating,
        int reviews,
        LocalDateTime creadoEn) {
}

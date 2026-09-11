package SkillLinkBackend.SkillLink.models.dto.catalogo;

/** Categoria con el total de publicaciones activas, para el catalogo publico. */
public record CategoriaCatalogoDto(Long id, String nombre, String descripcion, String icono, long servicios) {
}

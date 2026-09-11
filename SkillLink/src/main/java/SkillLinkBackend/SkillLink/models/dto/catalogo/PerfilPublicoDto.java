package SkillLinkBackend.SkillLink.models.dto.catalogo;

/** Perfil de un trabajador con datos de contacto (requiere autenticacion para verlos completos). */
public record PerfilPublicoDto(
        Long id,
        Long trabajadorId,
        String username,
        String descripcion,
        String tituloProfesional,
        Long categoriaId,
        String contacto,
        String telefono,
        String experiencia,
        String resumenCV) {
}

package SkillLinkBackend.SkillLink.models.dto.catalogo;

import java.util.List;

/** Usuario autenticado (token) resuelto contra el dominio (persona, cliente, trabajador, perfil). */
public record UsuarioActualDto(
        String tipo,
        Long personaId,
        Long clienteId,
        Long trabajadorId,
        Long perfilId,
        String nombre,
        String apPaterno,
        String email,
        String username,
        List<String> roles,
        List<String> authorities,
        boolean creadoEnEstaSesion) {
}

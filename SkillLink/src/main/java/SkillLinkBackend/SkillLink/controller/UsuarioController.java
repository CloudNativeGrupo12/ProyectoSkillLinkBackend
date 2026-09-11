package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.catalogo.UsuarioActualDto;
import SkillLinkBackend.SkillLink.services.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Identidad del usuario autenticado resuelta contra el dominio (crea la persona en el primer acceso). */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CatalogoService catalogoService;

    @GetMapping("/me")
    public UsuarioActualDto me() {
        return catalogoService.usuarioActual();
    }
}

package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.catalogo.CategoriaCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.CertificacionPublicaDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.PerfilPublicoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.ProfesionalCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.PublicacionCatalogoDto;
import SkillLinkBackend.SkillLink.models.requests.PublicarServicioRequest;
import SkillLinkBackend.SkillLink.services.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Catalogo publico consumido por la SPA (lecturas sin token) y operaciones
 * del usuario autenticado sobre sus propias publicaciones (scope de escritura).
 */
@RestController
@RequestMapping("/api/v1/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/categorias")
    public List<CategoriaCatalogoDto> categorias() {
        return catalogoService.listarCategorias();
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaCatalogoDto> categoria(@PathVariable Long id) {
        return catalogoService.obtenerCategoria(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/publicaciones")
    public List<PublicacionCatalogoDto> publicaciones(@RequestParam(required = false) Long categoriaId,
                                                      @RequestParam(required = false) Long trabajadorId,
                                                      @RequestParam(required = false, defaultValue = "false") boolean destacadas,
                                                      @RequestParam(required = false) Integer limite) {
        return catalogoService.listarPublicaciones(categoriaId, trabajadorId, destacadas, limite);
    }

    @GetMapping("/publicaciones/{id}")
    public PublicacionCatalogoDto publicacion(@PathVariable Long id) {
        return catalogoService.obtenerPublicacion(id);
    }

    /** Requiere Access Token con scope de escritura; el perfil se resuelve desde el token (onboarding). */
    @PostMapping("/publicaciones")
    public ResponseEntity<PublicacionCatalogoDto> publicar(@Valid @RequestBody PublicarServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.publicarServicio(request));
    }

    /** Requiere scope de escritura y ser dueno de la publicacion (o rol ADMIN). */
    @DeleteMapping("/publicaciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        catalogoService.eliminarPublicacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profesionales")
    public List<ProfesionalCatalogoDto> profesionales(
            @RequestParam(required = false, defaultValue = "false") boolean destacados) {
        return catalogoService.listarProfesionales(destacados);
    }

    @GetMapping("/profesionales/{trabajadorId}")
    public ResponseEntity<ProfesionalCatalogoDto> profesional(@PathVariable Long trabajadorId) {
        return catalogoService.obtenerProfesional(trabajadorId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/perfiles/por-trabajador/{trabajadorId}")
    public ResponseEntity<PerfilPublicoDto> perfil(@PathVariable Long trabajadorId) {
        return catalogoService.obtenerPerfilPorTrabajador(trabajadorId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/certificaciones/por-trabajador/{trabajadorId}")
    public List<CertificacionPublicaDto> certificaciones(@PathVariable Long trabajadorId) {
        return catalogoService.listarCertificacionesPorTrabajador(trabajadorId);
    }
}

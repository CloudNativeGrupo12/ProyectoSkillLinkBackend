package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.PublicacionDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPublicacion;
import SkillLinkBackend.SkillLink.models.requests.AgregarPublicacion;
import SkillLinkBackend.SkillLink.services.PublicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/publicaciones")
@RequiredArgsConstructor
public class PublicacionController {

    private final PublicacionService publicacionService;

    @GetMapping
    public List<PublicacionDto> buscar(@RequestParam(required = false) Long categoriaId,
                                       @RequestParam(required = false) Long comunaId,
                                       @RequestParam(required = false) BigDecimal precioMin,
                                       @RequestParam(required = false) BigDecimal precioMax,
                                       @RequestParam(required = false) String termino,
                                       @RequestParam(required = false) Long perfilId) {
        if (perfilId != null) {
            return publicacionService.obtenerPorPerfilId(perfilId);
        }
        return publicacionService.buscar(categoriaId, comunaId, precioMin, precioMax, termino);
    }

    @GetMapping("/{id}")
    public PublicacionDto obtenerPorId(@PathVariable Long id) {
        return publicacionService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PublicacionDto> crear(@Valid @RequestBody AgregarPublicacion request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicacionService.crear(request));
    }

    @PutMapping("/{id}")
    public PublicacionDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPublicacion request) {
        return publicacionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        publicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
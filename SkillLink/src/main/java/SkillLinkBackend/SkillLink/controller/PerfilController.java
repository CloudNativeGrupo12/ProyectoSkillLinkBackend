package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.PerfilDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPerfil;
import SkillLinkBackend.SkillLink.models.requests.AgregarPerfil;
import SkillLinkBackend.SkillLink.services.PerfilService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/perfiles")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping
    public List<PerfilDto> listar() {
        return perfilService.listar();
    }

    @GetMapping("/{id}")
    public PerfilDto obtenerPorId(@PathVariable Long id) {
        return perfilService.obtenerPorId(id);
    }

    @GetMapping("/por-username")
    public ResponseEntity<PerfilDto> obtenerPorUsername(@RequestParam String username) {
        return perfilService.obtenerPorUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/por-trabajador")
    public ResponseEntity<PerfilDto> obtenerPorTrabajadorId(@RequestParam Long trabajadorId) {
        return perfilService.obtenerPorTrabajadorId(trabajadorId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PerfilDto> crear(@Valid @RequestBody AgregarPerfil request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(perfilService.crear(request));
    }

    @PutMapping("/{id}")
    public PerfilDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPerfil request) {
        return perfilService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        perfilService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
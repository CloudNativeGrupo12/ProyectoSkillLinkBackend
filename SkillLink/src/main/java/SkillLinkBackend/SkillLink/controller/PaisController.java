package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.PaisDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPais;
import SkillLinkBackend.SkillLink.models.requests.AgregarPais;
import SkillLinkBackend.SkillLink.services.PaisService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paises")
@RequiredArgsConstructor
public class PaisController {

    private final PaisService paisService;

    @GetMapping
    public List<PaisDto> listar() {
        return paisService.listar();
    }

    @GetMapping("/{id}")
    public PaisDto obtenerPorId(@PathVariable Long id) {
        return paisService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PaisDto> crear(@Valid @RequestBody AgregarPais request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paisService.crear(request));
    }

    @PutMapping("/{id}")
    public PaisDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPais request) {
        return paisService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        paisService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
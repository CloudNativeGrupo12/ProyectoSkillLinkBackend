package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.MembresiaDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarMembresia;
import SkillLinkBackend.SkillLink.models.requests.AgregarMembresia;
import SkillLinkBackend.SkillLink.services.MembresiaService;
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
@RequestMapping("/api/v1/membresias")
@RequiredArgsConstructor
public class MembresiaController {

    private final MembresiaService membresiaService;

    @GetMapping
    public List<MembresiaDto> listar() {
        return membresiaService.listar();
    }

    @GetMapping("/{id}")
    public MembresiaDto obtenerPorId(@PathVariable Long id) {
        return membresiaService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<MembresiaDto> crear(@Valid @RequestBody AgregarMembresia request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membresiaService.crear(request));
    }

    @PutMapping("/{id}")
    public MembresiaDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarMembresia request) {
        return membresiaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        membresiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
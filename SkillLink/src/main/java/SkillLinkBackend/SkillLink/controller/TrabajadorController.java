package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.TrabajadorDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarTrabajador;
import SkillLinkBackend.SkillLink.models.requests.AgregarTrabajador;
import SkillLinkBackend.SkillLink.services.TrabajadorService;
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
@RequestMapping("/api/v1/trabajadores")
@RequiredArgsConstructor
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    @GetMapping
    public List<TrabajadorDto> listar() {
        return trabajadorService.listar();
    }

    @GetMapping("/{id}")
    public TrabajadorDto obtenerPorId(@PathVariable Long id) {
        return trabajadorService.obtenerPorId(id);
    }

    @GetMapping("/por-persona")
    public List<TrabajadorDto> obtenerPorPersonaId(@RequestParam Long personaId) {
        return trabajadorService.obtenerPorPersonaId(personaId);
    }

    @PostMapping
    public ResponseEntity<TrabajadorDto> crear(@Valid @RequestBody AgregarTrabajador request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trabajadorService.crear(request));
    }

    @PutMapping("/{id}")
    public TrabajadorDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarTrabajador request) {
        return trabajadorService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        trabajadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
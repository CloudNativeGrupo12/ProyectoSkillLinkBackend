package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.PersonaDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPersona;
import SkillLinkBackend.SkillLink.models.requests.AgregarPersona;
import SkillLinkBackend.SkillLink.services.PersonaService;
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
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    public List<PersonaDto> listar() {
        return personaService.listar();
    }

    @GetMapping("/{id}")
    public PersonaDto obtenerPorId(@PathVariable Long id) {
        return personaService.obtenerPorId(id);
    }

    @GetMapping("/me")
    public PersonaDto sesionActual() {
        return personaService.obtenerSesionActual();
    }

    @GetMapping("/por-email")
    public ResponseEntity<PersonaDto> obtenerPorEmail(@RequestParam String email) {
        return personaService.obtenerPorEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonaDto> crear(@Valid @RequestBody AgregarPersona request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personaService.crear(request));
    }

    @PutMapping("/{id}")
    public PersonaDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPersona request) {
        return personaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        personaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
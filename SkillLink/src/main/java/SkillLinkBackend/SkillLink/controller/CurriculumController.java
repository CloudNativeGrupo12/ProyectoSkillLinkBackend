package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.CurriculumDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCurriculum;
import SkillLinkBackend.SkillLink.models.requests.AgregarCurriculum;
import SkillLinkBackend.SkillLink.services.CurriculumService;
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
@RequestMapping("/api/v1/curriculums")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @GetMapping
    public List<CurriculumDto> listar(@RequestParam(required = false) Long trabajadorId) {
        if (trabajadorId != null) {
            return curriculumService.obtenerPorTrabajadorId(trabajadorId);
        }
        return curriculumService.listar();
    }

    @GetMapping("/{id}")
    public CurriculumDto obtenerPorId(@PathVariable Long id) {
        return curriculumService.obtenerPorId(id);
    }

    @GetMapping("/por-perfil")
    public ResponseEntity<CurriculumDto> obtenerPorPerfilId(@RequestParam Long perfilId) {
        return curriculumService.obtenerPorPerfilId(perfilId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CurriculumDto> crear(@Valid @RequestBody AgregarCurriculum request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(curriculumService.crear(request));
    }

    @PutMapping("/{id}")
    public CurriculumDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarCurriculum request) {
        return curriculumService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        curriculumService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.RegionDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarRegion;
import SkillLinkBackend.SkillLink.models.requests.AgregarRegion;
import SkillLinkBackend.SkillLink.services.RegionService;
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
@RequestMapping("/api/v1/regiones")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public List<RegionDto> listar(@RequestParam(required = false) Long paisId) {
        if (paisId != null) {
            return regionService.obtenerPorPaisId(paisId);
        }
        return regionService.listar();
    }

    @GetMapping("/{id}")
    public RegionDto obtenerPorId(@PathVariable Long id) {
        return regionService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<RegionDto> crear(@Valid @RequestBody AgregarRegion request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionService.crear(request));
    }

    @PutMapping("/{id}")
    public RegionDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarRegion request) {
        return regionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        regionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
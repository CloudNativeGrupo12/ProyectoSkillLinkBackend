package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.CiudadDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCiudad;
import SkillLinkBackend.SkillLink.models.requests.AgregarCiudad;
import SkillLinkBackend.SkillLink.services.CiudadService;
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
@RequestMapping("/api/v1/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService ciudadService;

    @GetMapping
    public List<CiudadDto> listar(@RequestParam(required = false) Long regionId) {
        if (regionId != null) {
            return ciudadService.obtenerPorRegionId(regionId);
        }
        return ciudadService.listar();
    }

    @GetMapping("/{id}")
    public CiudadDto obtenerPorId(@PathVariable Long id) {
        return ciudadService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<CiudadDto> crear(@Valid @RequestBody AgregarCiudad request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadService.crear(request));
    }

    @PutMapping("/{id}")
    public CiudadDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarCiudad request) {
        return ciudadService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ciudadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
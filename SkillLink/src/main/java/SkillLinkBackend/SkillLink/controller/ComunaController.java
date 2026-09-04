package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.ComunaDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarComuna;
import SkillLinkBackend.SkillLink.models.requests.AgregarComuna;
import SkillLinkBackend.SkillLink.services.ComunaService;
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
@RequestMapping("/api/v1/comunas")
@RequiredArgsConstructor
public class ComunaController {

    private final ComunaService comunaService;

    @GetMapping
    public List<ComunaDto> listar(@RequestParam(required = false) Long ciudadId) {
        if (ciudadId != null) {
            return comunaService.obtenerPorCiudadId(ciudadId);
        }
        return comunaService.listar();
    }

    @GetMapping("/{id}")
    public ComunaDto obtenerPorId(@PathVariable Long id) {
        return comunaService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<ComunaDto> crear(@Valid @RequestBody AgregarComuna request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comunaService.crear(request));
    }

    @PutMapping("/{id}")
    public ComunaDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarComuna request) {
        return comunaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comunaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
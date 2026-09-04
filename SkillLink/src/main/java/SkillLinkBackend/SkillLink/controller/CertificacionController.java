package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.CertificacionDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCertificacion;
import SkillLinkBackend.SkillLink.models.requests.AgregarCertificacion;
import SkillLinkBackend.SkillLink.services.CertificacionService;
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
@RequestMapping("/api/v1/certificaciones")
@RequiredArgsConstructor
public class CertificacionController {

    private final CertificacionService certificacionService;

    @GetMapping
    public List<CertificacionDto> listar(@RequestParam(required = false) Long perfilId) {
        if (perfilId != null) {
            return certificacionService.obtenerPorPerfilId(perfilId);
        }
        return certificacionService.listar();
    }

    @GetMapping("/{id}")
    public CertificacionDto obtenerPorId(@PathVariable Long id) {
        return certificacionService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<CertificacionDto> crear(@Valid @RequestBody AgregarCertificacion request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificacionService.crear(request));
    }

    @PutMapping("/{id}")
    public CertificacionDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarCertificacion request) {
        return certificacionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        certificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.SuscripcionInteresDto;
import SkillLinkBackend.SkillLink.models.requests.SuscribirRequest;
import SkillLinkBackend.SkillLink.services.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    /** Operacion publica de escritura (landing): registra interes en la membresia Premium. */
    @PostMapping("/public/suscripciones")
    public ResponseEntity<SuscripcionInteresDto> suscribir(@Valid @RequestBody SuscribirRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionService.suscribir(request));
    }

    /** Solo administradores (ROLE_ADMIN) pueden listar los interesados. */
    @GetMapping("/admin/suscripciones")
    public List<SuscripcionInteresDto> listar() {
        return suscripcionService.listar();
    }
}

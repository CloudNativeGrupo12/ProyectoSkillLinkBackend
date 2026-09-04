package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.ServicioDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarServicio;
import SkillLinkBackend.SkillLink.models.requests.AgregarServicio;
import SkillLinkBackend.SkillLink.services.ServicioService;
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
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public List<ServicioDto> listar(@RequestParam(required = false) String nombre,
                                    @RequestParam(required = false, defaultValue = "false") boolean soloActivos) {
        if (soloActivos) {
            return servicioService.listarActivos();
        }
        if (nombre != null && !nombre.isBlank()) {
            return servicioService.buscarPorNombre(nombre);
        }
        return servicioService.listar();
    }

    @GetMapping("/{id}")
    public ServicioDto obtenerPorId(@PathVariable Long id) {
        return servicioService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<ServicioDto> crear(@Valid @RequestBody AgregarServicio request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.crear(request));
    }

    @PutMapping("/{id}")
    public ServicioDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarServicio request) {
        return servicioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
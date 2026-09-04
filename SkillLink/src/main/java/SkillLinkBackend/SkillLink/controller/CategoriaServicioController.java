package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.CategoriaServicioDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCategoriaServicio;
import SkillLinkBackend.SkillLink.models.requests.AgregarCategoriaServicio;
import SkillLinkBackend.SkillLink.services.CategoriaServicioService;
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
@RequestMapping("/api/v1/categorias-servicio")
@RequiredArgsConstructor
public class CategoriaServicioController {

    private final CategoriaServicioService categoriaServicioService;

    @GetMapping
    public List<CategoriaServicioDto> listar() {
        return categoriaServicioService.listar();
    }

    @GetMapping("/{id}")
    public CategoriaServicioDto obtenerPorId(@PathVariable Long id) {
        return categoriaServicioService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<CategoriaServicioDto> crear(@Valid @RequestBody AgregarCategoriaServicio request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaServicioService.crear(request));
    }

    @PutMapping("/{id}")
    public CategoriaServicioDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarCategoriaServicio request) {
        return categoriaServicioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaServicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
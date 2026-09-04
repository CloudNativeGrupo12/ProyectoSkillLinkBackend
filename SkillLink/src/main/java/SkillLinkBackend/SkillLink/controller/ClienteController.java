package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.models.dto.ClienteDto;
import SkillLinkBackend.SkillLink.models.requests.ActualizarCliente;
import SkillLinkBackend.SkillLink.models.requests.AgregarCliente;
import SkillLinkBackend.SkillLink.services.ClienteService;
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
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteDto> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteDto obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id);
    }

    @GetMapping("/por-persona")
    public List<ClienteDto> obtenerPorPersonaId(@RequestParam Long personaId) {
        return clienteService.obtenerPorPersonaId(personaId);
    }

    @PostMapping
    public ResponseEntity<ClienteDto> crear(@Valid @RequestBody AgregarCliente request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(request));
    }

    @PutMapping("/{id}")
    public ClienteDto actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarCliente request) {
        return clienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
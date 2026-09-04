package SkillLinkBackend.SkillLink.services.impl;

import SkillLinkBackend.SkillLink.config.SecurityUtils;
import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import SkillLinkBackend.SkillLink.exceptions.ConflictoException;
import SkillLinkBackend.SkillLink.exceptions.RecursoNoEncontradoException;
import SkillLinkBackend.SkillLink.exceptions.SolicitudInvalidaException;
import SkillLinkBackend.SkillLink.models.dto.PerfilDto;
import SkillLinkBackend.SkillLink.models.entities.CategoriaServicio;
import SkillLinkBackend.SkillLink.models.entities.Cliente;
import SkillLinkBackend.SkillLink.models.entities.Perfil;
import SkillLinkBackend.SkillLink.models.entities.Persona;
import SkillLinkBackend.SkillLink.models.entities.Trabajador;
import SkillLinkBackend.SkillLink.models.requests.ActualizarPerfil;
import SkillLinkBackend.SkillLink.models.requests.AgregarPerfil;
import SkillLinkBackend.SkillLink.repositories.CategoriaServicioRepository;
import SkillLinkBackend.SkillLink.repositories.ClienteRepository;
import SkillLinkBackend.SkillLink.repositories.PerfilRepository;
import SkillLinkBackend.SkillLink.repositories.TrabajadorRepository;
import SkillLinkBackend.SkillLink.services.PerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private final PerfilRepository perfilRepository;
    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final CategoriaServicioRepository categoriaServicioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PerfilDto> listar() {
        return perfilRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilDto obtenerPorId(Long id) {
        return aDto(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PerfilDto> obtenerPorUsername(String username) {
        return perfilRepository.findByUsername(username).map(this::aDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PerfilDto> obtenerPorTrabajadorId(Long trabajadorId) {
        return perfilRepository.findFirstByTrabajadorId(trabajadorId).map(this::aDto);
    }

    @Override
    @Transactional
    public PerfilDto crear(AgregarPerfil request) {
        Perfil perfil = new Perfil();
        aplicarRequestCrear(perfil, request);
        return aDto(perfilRepository.save(perfil));
    }

    @Override
    @Transactional
    public PerfilDto actualizar(Long id, ActualizarPerfil request) {
        Perfil perfil = obtenerEntidad(id);
        verificarPropiedad(perfil);
        aplicarRequest(perfil, request);
        return aDto(perfilRepository.save(perfil));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Perfil perfil = obtenerEntidad(id);
        verificarPropiedad(perfil);
        perfilRepository.delete(perfil);
    }

    private void aplicarRequestCrear(Perfil perfil, AgregarPerfil request) {
        if (perfilRepository.existsByUsername(request.getUsername())) {
            throw new ConflictoException("Ya existe un perfil con el username " + request.getUsername());
        }
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id " + request.getClienteId()));
        Trabajador trabajador = trabajadorRepository.findById(request.getTrabajadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajador no encontrado con id " + request.getTrabajadorId()));
        validarPerfilEsDueno(cliente.getPersona(), trabajador.getPersona());
        perfil.setDescripcion(request.getDescripcion());
        perfil.setUsername(request.getUsername());
        perfil.setCliente(cliente);
        perfil.setTrabajador(trabajador);
        perfil.setCategoriaServicio(cargarCategoria(request.getCategoriaServicioId()));
        perfil.setTipoOfrecimientoId(request.getTipoOfrecimientoId());
    }

    private void aplicarRequest(Perfil perfil, ActualizarPerfil request) {
        perfilRepository.findByUsername(request.getUsername())
                .filter(existente -> !existente.getId().equals(perfil.getId()))
                .ifPresent(existente -> {
                    throw new ConflictoException("Ya existe un perfil con el username " + request.getUsername());
                });
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id " + request.getClienteId()));
        Trabajador trabajador = trabajadorRepository.findById(request.getTrabajadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Trabajador no encontrado con id " + request.getTrabajadorId()));
        validarPerfilEsDueno(cliente.getPersona(), trabajador.getPersona());
        perfil.setDescripcion(request.getDescripcion());
        perfil.setUsername(request.getUsername());
        perfil.setCliente(cliente);
        perfil.setTrabajador(trabajador);
        perfil.setCategoriaServicio(cargarCategoria(request.getCategoriaServicioId()));
        perfil.setTipoOfrecimientoId(request.getTipoOfrecimientoId());
    }

    private void validarPerfilEsDueno(Persona personaCliente, Persona personaTrabajador) {
        String emailToken = SecurityUtils.emailObligatorio();
        if (personaTrabajador != null && !emailToken.equals(personaTrabajador.getEmail().toLowerCase())) {
            throw new AccionNoPermitidaException("El perfil pertenece a un trabajador distinto del usuario autenticado.");
        }
        if (personaCliente != null && !emailToken.equals(personaCliente.getEmail().toLowerCase())) {
            throw new AccionNoPermitidaException("El perfil pertenece a un cliente distinto del usuario autenticado.");
        }
    }

    private void verificarPropiedad(Perfil perfil) {
        if (perfil.getTrabajador() == null) {
            throw new SolicitudInvalidaException("El perfil no tiene trabajador asociado.");
        }
        validarPerfilEsDueno(perfil.getCliente() != null ? perfil.getCliente().getPersona() : null,
                perfil.getTrabajador().getPersona());
    }

    private CategoriaServicio cargarCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaServicioRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada con id " + categoriaId));
    }

    private Perfil obtenerEntidad(Long id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Perfil no encontrado con id " + id));
    }

    private PerfilDto aDto(Perfil perfil) {
        return new PerfilDto(
                perfil.getId(),
                perfil.getDescripcion(),
                perfil.getUsername(),
                perfil.getCliente() != null ? perfil.getCliente().getId() : null,
                perfil.getTrabajador() != null ? perfil.getTrabajador().getId() : null,
                perfil.getCategoriaServicio() != null ? perfil.getCategoriaServicio().getId() : null,
                perfil.getTipoOfrecimientoId());
    }
}
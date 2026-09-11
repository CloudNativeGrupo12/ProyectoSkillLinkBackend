package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.catalogo.CategoriaCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.CertificacionPublicaDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.PerfilPublicoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.ProfesionalCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.PublicacionCatalogoDto;
import SkillLinkBackend.SkillLink.models.dto.catalogo.UsuarioActualDto;
import SkillLinkBackend.SkillLink.models.requests.PublicarServicioRequest;

import java.util.List;
import java.util.Optional;

/**
 * Modelo de lectura orientado al frontend (catalogo publico) y operaciones
 * de alto nivel del usuario autenticado (onboarding y publicar servicio).
 */
public interface CatalogoService {

    List<CategoriaCatalogoDto> listarCategorias();

    Optional<CategoriaCatalogoDto> obtenerCategoria(Long id);

    List<PublicacionCatalogoDto> listarPublicaciones(Long categoriaId, Long trabajadorId, boolean soloDestacadas,
                                                     Integer limite);

    PublicacionCatalogoDto obtenerPublicacion(Long id);

    List<ProfesionalCatalogoDto> listarProfesionales(boolean soloDestacados);

    Optional<ProfesionalCatalogoDto> obtenerProfesional(Long trabajadorId);

    Optional<PerfilPublicoDto> obtenerPerfilPorTrabajador(Long trabajadorId);

    List<CertificacionPublicaDto> listarCertificacionesPorTrabajador(Long trabajadorId);

    UsuarioActualDto usuarioActual();

    PublicacionCatalogoDto publicarServicio(PublicarServicioRequest request);

    void eliminarPublicacion(Long id);
}

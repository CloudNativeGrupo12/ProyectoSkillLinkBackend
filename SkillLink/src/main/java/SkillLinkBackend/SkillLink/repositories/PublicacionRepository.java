package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByPerfilId(Long perfilId);

    List<Publicacion> findByPerfilTrabajadorIdOrderByCreadoEnDesc(Long trabajadorId);

    List<Publicacion> findByPerfilCategoriaServicioIdOrderByCalificacionPromedioDesc(Long categoriaId);

    List<Publicacion> findAllByOrderByCalificacionPromedioDesc();

    long countByPerfilCategoriaServicioId(Long categoriaId);

    @Query("""
            SELECT DISTINCT p FROM Publicacion p
            JOIN p.perfil pf
            LEFT JOIN pf.trabajador t
            LEFT JOIN t.comunas c
            WHERE (:categoriaId IS NULL OR pf.categoriaServicio.id = :categoriaId)
              AND (:comunaId IS NULL OR c.id = :comunaId)
              AND (:precioMin IS NULL OR p.precioMax >= :precioMin)
              AND (:precioMax IS NULL OR p.precioMin <= :precioMax)
              AND (:termino IS NULL OR LOWER(p.tipoPublicacion) LIKE LOWER(CONCAT('%', :termino, '%'))
                   OR LOWER(COALESCE(p.titulo, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                   OR LOWER(COALESCE(p.descripcion, '')) LIKE LOWER(CONCAT('%', :termino, '%')))
            """)
    List<Publicacion> buscar(
            @Param("categoriaId") Long categoriaId,
            @Param("comunaId") Long comunaId,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("termino") String termino);
}
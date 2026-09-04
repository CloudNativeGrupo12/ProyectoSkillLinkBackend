package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    boolean existsByEmail(String email);

    Optional<Persona> findByEmail(String email);

    @Query("select distinct p from Persona p "
            + "left join fetch p.clientes "
            + "left join fetch p.trabajadores "
            + "where lower(p.email) = lower(:email)")
    Optional<Persona> findByEmailConRoles(@Param("email") String email);
}
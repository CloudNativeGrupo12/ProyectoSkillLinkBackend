package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    boolean existsByEmail(String email);

    Optional<Persona> findByEmail(String email);

    /** Los roles (clientes/trabajadores) se cargan de forma perezosa dentro de la transaccion. */
    @Query("select p from Persona p where lower(p.email) = lower(:email)")
    Optional<Persona> findByEmailConRoles(@Param("email") String email);
}
package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    boolean existsByUsername(String username);

    Optional<Perfil> findByUsername(String username);

    Optional<Perfil> findFirstByTrabajadorId(Long trabajadorId);
}
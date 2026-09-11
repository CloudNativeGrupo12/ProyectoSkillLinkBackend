package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.SuscripcionInteres;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuscripcionInteresRepository extends JpaRepository<SuscripcionInteres, Long> {

    Optional<SuscripcionInteres> findByEmailIgnoreCase(String email);
}

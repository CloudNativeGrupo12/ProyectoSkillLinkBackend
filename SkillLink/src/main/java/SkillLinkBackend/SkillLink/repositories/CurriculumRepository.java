package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    List<Curriculum> findByTrabajadorId(Long trabajadorId);

    Optional<Curriculum> findByPerfilId(Long perfilId);

    boolean existsByPerfilId(Long perfilId);
}
package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long> {

    List<Trabajador> findByPersonaId(Long personaId);

    boolean existsByPersonaIdAndMembresiaId(Long personaId, Long membresiaId);
}
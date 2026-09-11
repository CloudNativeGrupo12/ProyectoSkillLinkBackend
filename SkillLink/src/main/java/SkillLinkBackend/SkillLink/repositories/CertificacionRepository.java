package SkillLinkBackend.SkillLink.repositories;

import SkillLinkBackend.SkillLink.models.entities.Certificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificacionRepository extends JpaRepository<Certificacion, Long> {

    List<Certificacion> findByPerfilId(Long perfilId);

    List<Certificacion> findByPerfilTrabajadorId(Long trabajadorId);
}
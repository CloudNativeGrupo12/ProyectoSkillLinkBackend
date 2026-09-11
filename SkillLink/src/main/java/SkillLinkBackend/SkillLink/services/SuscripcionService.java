package SkillLinkBackend.SkillLink.services;

import SkillLinkBackend.SkillLink.models.dto.SuscripcionInteresDto;
import SkillLinkBackend.SkillLink.models.requests.SuscribirRequest;

import java.util.List;

public interface SuscripcionService {

    SuscripcionInteresDto suscribir(SuscribirRequest request);

    List<SuscripcionInteresDto> listar();
}

package SkillLinkBackend.SkillLink.models.dto;

import java.time.LocalDateTime;

public record SuscripcionInteresDto(Long id, String email, String tipo, LocalDateTime creadoEn) {
}

package SkillLinkBackend.SkillLink.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorDto {

    private Long id;
    private Long personaId;
    private Long membresiaId;
    private List<Long> certificacionIds;
    private List<Long> servicioIds;
    private List<Long> comunaIds;
    private List<Long> perfilIds;

}
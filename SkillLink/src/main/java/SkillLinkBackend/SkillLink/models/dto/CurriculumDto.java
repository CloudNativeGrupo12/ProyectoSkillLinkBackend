package SkillLinkBackend.SkillLink.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumDto {

    private Long id;
    private String resumen;
    private String experienciaJson;
    private Long trabajadorId;
    private Long perfilId;

}
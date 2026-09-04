package SkillLinkBackend.SkillLink.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComunaDto {

    private Long id;
    private String nombre;
    private Long ciudadId;

}
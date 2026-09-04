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
public class ServicioDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean isActivo;
    private List<Long> categoriaIds;

}
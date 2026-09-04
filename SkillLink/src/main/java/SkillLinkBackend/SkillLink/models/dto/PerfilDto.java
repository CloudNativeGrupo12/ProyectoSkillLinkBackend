package SkillLinkBackend.SkillLink.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDto {

    private Long id;
    private String descripcion;
    private String username;
    private Long clienteId;
    private Long trabajadorId;
    private Long categoriaServicioId;
    private Integer tipoOfrecimientoId;

}
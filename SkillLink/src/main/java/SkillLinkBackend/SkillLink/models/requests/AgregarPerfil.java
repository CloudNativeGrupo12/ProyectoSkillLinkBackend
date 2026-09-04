package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgregarPerfil {

    private String descripcion;

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long trabajadorId;

    private Long categoriaServicioId;

    private Integer tipoOfrecimientoId;

}
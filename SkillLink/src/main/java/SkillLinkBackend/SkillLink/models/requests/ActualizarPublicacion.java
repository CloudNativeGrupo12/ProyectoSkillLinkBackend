package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPublicacion {

    @NotBlank
    @Size(max = 50)
    private String tipoPublicacion;

    @Size(max = 150)
    private String titulo;

    @Size(max = 2000)
    private String descripcion;

    @Size(max = 20)
    private String estado;

    @NotNull
    private Long perfilId;

    private Long curriculumId;

    private Long servicioId;

    @NotNull
    @PositiveOrZero
    private BigDecimal precioMin;

    @NotNull
    @PositiveOrZero
    private BigDecimal precioMax;

    @NotNull
    @PositiveOrZero
    private BigDecimal tipoPrecio;

    @Size(max = 30)
    private String modalidadPrecio;

    @NotBlank
    @Size(max = 25)
    private String moneda;

    @NotBlank
    @Size(max = 50)
    private String duracionEstimada;

}

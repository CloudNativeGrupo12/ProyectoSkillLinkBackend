package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Formulario simplificado "Publicar servicio" del frontend: el perfil se resuelve desde el token. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicarServicioRequest {

    @NotBlank
    @Size(max = 150)
    private String titulo;

    @NotBlank
    @Size(max = 2000)
    private String descripcion;

    @NotNull
    private Long categoriaId;

    @NotNull
    @Positive
    private BigDecimal precio;

    @Size(max = 100)
    private String nombreAutor;

    @Size(max = 30)
    private String modalidadPrecio;

    @Size(max = 50)
    private String duracionEstimada;

}

package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCertificacion {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    private String entidadEmisora;

    @NotNull
    private LocalDate fechaEmision;

    @NotBlank
    @Size(max = 255)
    private String urlVerificacion;

    @NotNull
    private Long perfilId;

}
package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarCurriculum {

    @NotBlank
    private String resumen;

    @NotBlank
    private String experienciaJson;

    @NotNull
    private Long trabajadorId;

    @NotNull
    private Long perfilId;

}
package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarTrabajador {

    @NotNull
    private Long personaId;

    @NotNull
    private Long membresiaId;

    private List<Long> certificacionIds = new ArrayList<>();

    private List<Long> servicioIds = new ArrayList<>();

    private List<Long> comunaIds = new ArrayList<>();

}
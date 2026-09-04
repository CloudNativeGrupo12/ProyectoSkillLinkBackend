package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class ActualizarServicio {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    private String descripcion;

    private boolean isActivo = true;

    private List<Long> categoriaIds = new ArrayList<>();

}
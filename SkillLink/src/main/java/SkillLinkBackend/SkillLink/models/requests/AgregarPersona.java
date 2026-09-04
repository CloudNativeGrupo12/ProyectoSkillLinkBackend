package SkillLinkBackend.SkillLink.models.requests;

import jakarta.validation.constraints.Email;
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
public class AgregarPersona {

    @NotBlank
    @Size(max = 50)
    private String pNombre;

    @Size(max = 50)
    private String sNombre;

    @NotBlank
    @Size(max = 50)
    private String apPaterno;

    @NotBlank
    @Size(max = 50)
    private String apMaterno;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 25)
    private String telefono;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotNull
    private Long membresiaId;

}
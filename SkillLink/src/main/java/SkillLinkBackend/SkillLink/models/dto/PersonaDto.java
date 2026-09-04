package SkillLinkBackend.SkillLink.models.dto;

import SkillLinkBackend.SkillLink.models.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDto {

    private Long id;
    private String pNombre;
    private String sNombre;
    private String apPaterno;
    private String apMaterno;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private Long membresiaId;
    private List<Long> clienteIds = new ArrayList<>();
    private List<Long> trabajadorIds = new ArrayList<>();
    private List<Rol> roles = new ArrayList<>();

}
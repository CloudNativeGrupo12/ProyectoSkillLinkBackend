package SkillLinkBackend.SkillLink.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificacionDto {

    private Long id;
    private String nombre;
    private String entidadEmisora;
    private LocalDate fechaEmision;
    private String urlVerificacion;
    private Long perfilId;

}
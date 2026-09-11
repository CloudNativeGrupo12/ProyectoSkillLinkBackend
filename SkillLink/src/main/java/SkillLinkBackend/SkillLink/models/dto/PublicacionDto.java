package SkillLinkBackend.SkillLink.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionDto {

    private Long id;
    private String tipoPublicacion;
    private String titulo;
    private String descripcion;
    private String estado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private Long perfilId;
    private Long curriculumId;
    private Long servicioId;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private BigDecimal tipoPrecio;
    private String modalidadPrecio;
    private String moneda;
    private String duracionEstimada;
    private BigDecimal calificacionPromedio;
    private Integer totalResenas;

}

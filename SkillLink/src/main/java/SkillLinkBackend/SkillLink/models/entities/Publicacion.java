package SkillLinkBackend.SkillLink.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "publicaciones", indexes = @Index(name = "idx_publicaciones_tipo", columnList = "tipo_publicacion"))
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_publicacion", nullable = false, length = 50)
    private String tipoPublicacion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    @Column(length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 20)
    private String estado = "activo";

    @Column(name = "modalidad_precio", length = 30)
    private String modalidadPrecio;

    @Column(name = "calificacion_promedio", precision = 3, scale = 2)
    private BigDecimal calificacionPromedio = BigDecimal.ZERO;

    @Column(name = "total_resenas")
    private Integer totalResenas = 0;

    @Column(name = "precio_min", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMin;

    @Column(name = "precio_max", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioMax;

    @Column(name = "tipo_precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal tipoPrecio;

    @Column(nullable = false, length = 25)
    private String moneda;

    @Column(name = "duracion_estimada", nullable = false, length = 50)
    private String duracionEstimada;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.creadoEn = now;
        this.actualizadoEn = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

}
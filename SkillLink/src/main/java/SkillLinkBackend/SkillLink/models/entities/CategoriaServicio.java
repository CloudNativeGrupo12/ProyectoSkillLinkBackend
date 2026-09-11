package SkillLinkBackend.SkillLink.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categorias_servicio", indexes = @Index(name = "idx_categorias_servicio_nombre", columnList = "nombre"))
public class CategoriaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Clave del icono que renderiza el frontend (ej: fitness, programacion). */
    @Column(length = 50)
    private String icono;

    @ManyToMany(mappedBy = "categorias")
    private List<Servicio> servicios = new ArrayList<>();

    @OneToMany(mappedBy = "categoriaServicio")
    private List<Perfil> perfiles = new ArrayList<>();

}
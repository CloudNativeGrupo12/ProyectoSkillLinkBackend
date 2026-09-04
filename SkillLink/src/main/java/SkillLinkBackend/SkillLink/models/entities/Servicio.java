package SkillLinkBackend.SkillLink.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "servicios", indexes = @Index(name = "idx_servicios_nombre", columnList = "nombre"))
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "is_activo", nullable = false)
    private boolean isActivo = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "servicios_categorias",
            joinColumns = @JoinColumn(name = "servicio_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_servicio_id"))
    private List<CategoriaServicio> categorias = new ArrayList<>();

    @ManyToMany(mappedBy = "servicios")
    private List<Trabajador> trabajadores = new ArrayList<>();

    @ManyToMany(mappedBy = "servicios")
    private List<Cliente> clientes = new ArrayList<>();

}
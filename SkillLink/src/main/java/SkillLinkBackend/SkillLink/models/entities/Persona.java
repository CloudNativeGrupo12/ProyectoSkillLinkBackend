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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "personas", indexes = @Index(name = "idx_personas_email", columnList = "email"))
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "p_nombre", nullable = false, length = 50)
    private String pNombre;

    @Column(name = "s_nombre", length = 50)
    private String sNombre;

    @Column(name = "ap_paterno", nullable = false, length = 50)
    private String apPaterno;

    @Column(name = "ap_materno", nullable = false, length = 50)
    private String apMaterno;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 25)
    private String telefono;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membresia_id", nullable = false)
    private Membresia membresia;

    @OneToMany(mappedBy = "persona")
    private List<Cliente> clientes = new ArrayList<>();

    @OneToMany(mappedBy = "persona")
    private List<Trabajador> trabajadores = new ArrayList<>();

}
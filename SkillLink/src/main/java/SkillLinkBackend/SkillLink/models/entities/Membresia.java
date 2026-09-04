package SkillLinkBackend.SkillLink.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "membresias")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @OneToMany(mappedBy = "membresia")
    private List<Persona> personas = new ArrayList<>();

    @OneToMany(mappedBy = "membresia")
    private List<Cliente> clientes = new ArrayList<>();

    @OneToMany(mappedBy = "membresia")
    private List<Trabajador> trabajadores = new ArrayList<>();

}
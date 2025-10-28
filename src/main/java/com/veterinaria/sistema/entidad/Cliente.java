package com.veterinaria.sistema.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id;
    private String nombre;
    private String apellido;
    private String direccion;
    private String telefono;
    private String email;

    @OneToMany(mappedBy = "cliente")
    private List<Mascota> mascotas;
}

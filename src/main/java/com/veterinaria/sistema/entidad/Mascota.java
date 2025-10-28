package com.veterinaria.sistema.entidad;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Integer idMascota;
    private String nombre;
    private String especie;
    private String raza;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    private String sexo;
    @ManyToOne
    @JoinColumn(name = "Cliente_id_cliente", nullable = false)
    private Cliente cliente;
}

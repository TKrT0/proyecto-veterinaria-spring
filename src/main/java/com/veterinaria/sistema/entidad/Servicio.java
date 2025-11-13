package com.veterinaria.sistema.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idServicio")
    private Integer idServicio;
    @Column(nullable = false, length = 200)
    private String nombre;
    @Column(nullable = false, length = 200)
    private String descripcion;
    @Column(nullable = false)
    private Double precio;
}

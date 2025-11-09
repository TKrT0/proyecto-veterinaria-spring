package com.veterinaria.sistema.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "historial_clinico")
public class HistorialClinico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDateTime fechaVisita;

    @Column(name = "diagnostico")
    private String diagnostico;

    @Column(name = "tratamiento", length = 200)
    private String tratamiento;

    @Column(name = "notas", length = 200)
    private String notas;

    @ManyToOne
    @JoinColumn(name = "Mascota_id_mascota", nullable = false)
    private Mascota mascota;
}

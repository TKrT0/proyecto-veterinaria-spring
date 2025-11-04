package com.veterinaria.sistema.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;

    // Usamos LocalDateTime para guardar fecha Y hora
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    private String motivo;

    private String estado; // Ej. "Pendiente", "Completada", "Cancelada"
    // (Opcional: puedes usar un Enum para el estado)

    @ManyToOne
    @JoinColumn(name = "Mascota_id_mascota", nullable = false)
    private Mascota mascota;

    // Al tener la mascota, puedes acceder al cliente con mascota.getCliente()
}
package com.veterinaria.sistema.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idFactura")
    private Integer idFactura;

    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, length = 30)
    private String estado;  // Esto es por si se maneja de pendiente, completado, etc

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true) // Para borrar las citas relacionadas
    private List<LineaFactura> lineas;
}

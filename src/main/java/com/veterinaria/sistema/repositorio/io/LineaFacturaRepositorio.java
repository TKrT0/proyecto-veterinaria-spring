package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.LineaFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineaFacturaRepositorio extends JpaRepository<LineaFactura, Integer> {
    @Query("SELECT l.servicio.nombre, SUM(l.subtotal) as ventasTotales " +
            "FROM LineaFactura l " +
            "WHERE l.factura.estado = 'Pagada' " +
            "GROUP BY l.servicio.nombre " +
            "ORDER BY ventasTotales DESC")
    List<Object[]> obtenerTopServiciosVendidos();
}

package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FacturaRepositorio extends JpaRepository<Factura, Integer> {
    List<Factura> findByClienteId(Integer clienteId);

    @Query("SELECT f FROM Factura f LEFT JOIN FETCH f.lineas WHERE f.idFactura = :idFactura")
    Optional<Factura> findByIdWithLineas(Integer idFactura);

    @Query("SELECT SUM(f.total) FROM Factura f WHERE f.estado = 'Pagada'")
    Double sumarVentasTotalesPagadas();
}

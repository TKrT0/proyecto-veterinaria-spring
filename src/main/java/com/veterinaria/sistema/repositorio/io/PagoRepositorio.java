package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepositorio extends JpaRepository<Pago, Integer> {
}

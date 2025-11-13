package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepositorio extends JpaRepository<Servicio, Integer> {
}

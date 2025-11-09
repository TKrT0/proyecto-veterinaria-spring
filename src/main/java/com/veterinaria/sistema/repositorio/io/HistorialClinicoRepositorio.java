package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialClinicoRepositorio extends JpaRepository<HistorialClinico, Integer> {
    List<HistorialClinico> findByMascotaIdMascotaOrderByFechaVisitaDesc(Integer mascotaIdMascota);
}

package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepositorio extends JpaRepository<Cita, Integer> {
    List<Cita> findByMascotaIdMascota(Integer idMascota);
    List<Cita> findByFechaHoraBetween(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin);
    List<Cita> findByMascotaClienteId(Integer idCliente);
}

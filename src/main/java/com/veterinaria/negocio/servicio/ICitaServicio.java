package com.veterinaria.negocio.servicio;

import com.veterinaria.sistema.entidad.Cita;
import java.time.LocalDate;
import java.util.List;

public interface ICitaServicio {

    // Programa una nueva cita y la asocia a una mascota
    Cita programarCita(Cita cita, Integer idMascota);

    // Lista todas las citas de una mascota
    List<Cita> listarCitasPorMascota(Integer idMascota);

    // Lista todas las citas de un día específico
    List<Cita> listarCitasDelDia(LocalDate fecha);

    // Lista todas las citas de un cliente
    List<Cita> listarCitasPorCliente(Integer idCliente);

    // Cancela o actualiza el estado de una cita
    Cita actualizarEstadoCita(Integer idCita, String nuevoEstado);

    void eliminarCita(Integer idCita);

    List<Cita> listarTodasLasCitas();
}
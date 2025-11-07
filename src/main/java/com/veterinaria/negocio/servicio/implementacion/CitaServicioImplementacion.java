package com.veterinaria.negocio.servicio.implementacion;

import com.veterinaria.negocio.servicio.ICitaServicio;
import com.veterinaria.sistema.entidad.Cita;
import com.veterinaria.sistema.entidad.Mascota;
import com.veterinaria.sistema.repositorio.io.CitaRepositorio;
import com.veterinaria.sistema.repositorio.io.MascotaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaServicioImplementacion implements ICitaServicio {

    @Autowired
    private CitaRepositorio citaRepositorio; // Para guardar, buscar, eliminar Citas

    @Autowired
    private MascotaRepositorio mascotaRepositorio; // Para encontrar la Mascota dueña de la cita

    /**
     * Lógica:
     * 1. Busca la mascota por su ID.
     * 2. Si la mascota existe, le asigna esa mascota a la cita.
     * 3. Establece un estado por defecto (ej. "Pendiente").
     * 4. Guarda la cita en la base de datos.
     */
    @Override
    public Cita programarCita(Cita cita, Integer idMascota) {
        // Buscamos la mascota a la que pertenece la cita
        Mascota mascota = mascotaRepositorio.findById(idMascota).orElse(null);

        if (mascota != null) {
            cita.setMascota(mascota); // Asignamos la mascota
            cita.setEstado("Pendiente"); // Estado por defecto
            return citaRepositorio.save(cita);
        }
        // Si no se encontró la mascota, no se puede crear la cita
        return null;
    }

    /**
     * Lógica:
     * 1. Llama al método del repositorio que busca por el ID de la mascota.
     */
    @Override
    public List<Cita> listarCitasPorMascota(Integer idMascota) {
        return citaRepositorio.findByMascotaIdMascota(idMascota);
    }

    /**
     * Lógica:
     * 1. Define el rango de un día (desde las 00:00 hasta las 23:59).
     * 2. Llama al método del repositorio que busca citas entre esas dos fechas/horas.
     */
    @Override
    public List<Cita> listarCitasDelDia(LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay(); // Ej. 2025-11-03 00:00:00
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX); // Ej. 2025-11-03 23:59:59
        return citaRepositorio.findByFechaHoraBetween(inicioDelDia, finDelDia);
    }

    /**
     * Lógica:
     * 1. Llama al método del repositorio que busca por el ID del cliente (dueño de la mascota).
     */
    @Override
    public List<Cita> listarCitasPorCliente(Integer idCliente) {
        return citaRepositorio.findByMascotaClienteId(idCliente);
    }

    /**
     * Lógica:
     * 1. Busca la cita por su ID.
     * 2. Si la encuentra, actualiza solo el campo "estado".
     * 3. Guarda los cambios en la BD.
     */
    @Override
    public Cita actualizarEstadoCita(Integer idCita, String nuevoEstado) {
        Optional<Cita> citaOptional = citaRepositorio.findById(idCita);
        if (citaOptional.isPresent()) {
            Cita cita = citaOptional.get();
            cita.setEstado(nuevoEstado); // Actualiza el estado
            return citaRepositorio.save(cita);
        }
        return null; // No se encontró la cita
    }

    /**
     * Lógica:
     * 1. Llama al método del repositorio para eliminar por ID.
     */
    @Override
    public void eliminarCita(Integer idCita) {
        citaRepositorio.deleteById(idCita);
    }

    @Override
    public List<Cita> listarTodasLasCitas() {
        // Simplemente usamos el método findAll() de JpaRepository
        // Ordenado por ID descendente para ver las más nuevas primero
        return citaRepositorio.findAll(Sort.by(Sort.Direction.DESC, "idCita"));
    }
}
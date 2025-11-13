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
    private CitaRepositorio citaRepositorio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Override
    public Cita programarCita(Cita cita, Integer idMascota) {

        Mascota mascota = mascotaRepositorio.findById(idMascota).orElse(null);

        if (mascota != null) {
            cita.setMascota(mascota);
            cita.setEstado("Pendiente");
            return citaRepositorio.save(cita);
        }
        return null;
    }

    @Override
    public List<Cita> listarCitasPorMascota(Integer idMascota) {
        return citaRepositorio.findByMascotaIdMascota(idMascota);
    }

    @Override
    public List<Cita> listarCitasDelDia(LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX);
        return citaRepositorio.findByFechaHoraBetween(inicioDelDia, finDelDia);
    }

    @Override
    public List<Cita> listarCitasPorCliente(Integer idCliente) {
        return citaRepositorio.findByMascotaClienteId(idCliente);
    }

    @Override
    public Cita actualizarEstadoCita(Integer idCita, String nuevoEstado) {
        Optional<Cita> citaOptional = citaRepositorio.findById(idCita);
        if (citaOptional.isPresent()) {
            Cita cita = citaOptional.get();
            cita.setEstado(nuevoEstado);
            return citaRepositorio.save(cita);
        }
        return null;

    }

    @Override
    public void eliminarCita(Integer idCita) {
        citaRepositorio.deleteById(idCita);
    }

    @Override
    public List<Cita> listarTodasLasCitas() {
        return citaRepositorio.findAll(Sort.by(Sort.Direction.DESC, "idCita"));
    }
}
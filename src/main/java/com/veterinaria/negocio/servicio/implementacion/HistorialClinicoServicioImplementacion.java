package com.veterinaria.negocio.servicio.implementacion;

import com.veterinaria.negocio.servicio.IHistorialClinicoServicio;
import com.veterinaria.sistema.entidad.HistorialClinico;
import com.veterinaria.sistema.entidad.Mascota;
import com.veterinaria.sistema.repositorio.io.HistorialClinicoRepositorio;
import com.veterinaria.sistema.repositorio.io.MascotaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistorialClinicoServicioImplementacion implements IHistorialClinicoServicio {
    @Autowired
    private HistorialClinicoRepositorio historialClinicorepositorio;
    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Override
    public List<HistorialClinico> listarHistorialPorMascota(Integer idMascota) {
        return historialClinicorepositorio.findByMascotaIdMascotaOrderByFechaVisitaDesc(idMascota);
    }

    @Override
    public HistorialClinico agregarEntradaHistorial(HistorialClinico entrada, Integer idMascota) {
        Mascota mascota = mascotaRepositorio.findById(idMascota).orElse(null);

        if(mascota != null){
            entrada.setMascota(mascota);
            if(entrada.getFechaVisita() == null){
                entrada.setFechaVisita(LocalDateTime.now());
            }
            return historialClinicorepositorio.save(entrada);
        }
        return null;
    }
}

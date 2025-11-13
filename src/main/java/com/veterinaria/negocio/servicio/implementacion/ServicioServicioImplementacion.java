package com.veterinaria.negocio.servicio.implementacion;

import com.veterinaria.sistema.entidad.Servicio;
import com.veterinaria.negocio.servicio.IServicioServicio;
import com.veterinaria.sistema.repositorio.io.ServicioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ServicioServicioImplementacion implements IServicioServicio {

    @Autowired
    private ServicioRepositorio servicioRepositorio;

    @Override
    public List<Servicio> listarServicios() {
        return servicioRepositorio.findAll();
    }

    @Override
    public Servicio guardarServicio(Servicio servicio) {
        return servicioRepositorio.save(servicio);
    }

    @Override
    public void eliminarServicio(Integer idServicio) {
        servicioRepositorio.deleteById(idServicio);
    }

    @Override
    public Servicio buscarServicioPorId(Integer idServicio) {
        return servicioRepositorio.findById(idServicio).orElse(null);
    }
}

package com.veterinaria.negocio.servicio;

import com.veterinaria.sistema.entidad.Servicio;

import java.util.List;

public interface IServicioServicio {
    List<Servicio> listarServicios();

    Servicio guardarServicio(Servicio servicio);

    void eliminarServicio(Integer idServicio);

    Servicio buscarServicioPorId(Integer idServicio);
}

package com.veterinaria.negocio.servicio;

import com.veterinaria.sistema.entidad.HistorialClinico;

import java.util.List;

public interface IHistorialClinicoServicio {
    List<HistorialClinico> listarHistorialPorMascota(Integer idMascota);
    HistorialClinico agregarEntradaHistorial(HistorialClinico entrada, Integer idMascota);
}

package com.veterinaria.negocio.servicio;

import com.veterinaria.negocio.dto.ConteoPorEspecieDTO;
import com.veterinaria.negocio.dto.VentasPorServicioDTO;

import java.util.List;

public interface IDashboardServicio {

    Long obtenerTotalClientes();

    Double obtenerTotalVentas();

    List<ConteoPorEspecieDTO> obtenerConteoPorEspecie();

    List<VentasPorServicioDTO> obtenerTopServicios();
}

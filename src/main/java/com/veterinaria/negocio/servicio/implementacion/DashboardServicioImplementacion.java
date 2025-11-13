package com.veterinaria.negocio.servicio.implementacion;

import com.veterinaria.negocio.dto.ConteoPorEspecieDTO;
import com.veterinaria.negocio.dto.VentasPorServicioDTO;
import com.veterinaria.negocio.servicio.IDashboardServicio;
import com.veterinaria.sistema.repositorio.io.ClienteRepositorio;
import com.veterinaria.sistema.repositorio.io.FacturaRepositorio;
import com.veterinaria.sistema.repositorio.io.LineaFacturaRepositorio;
import com.veterinaria.sistema.repositorio.io.MascotaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServicioImplementacion implements IDashboardServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private MascotaRepositorio mascotaRepositorio;
    @Autowired
    private FacturaRepositorio facturaRepositorio;
    @Autowired
    private LineaFacturaRepositorio lineaFacturaRepositorio;

    @Override
    public Long obtenerTotalClientes() {
        return clienteRepositorio.contarTotalClientes();
    }

    @Override
    public Double obtenerTotalVentas() {
        Double total = facturaRepositorio.sumarVentasTotalesPagadas();
        return (total == null) ? 0.0 : total;
    }

    @Override
    public List<ConteoPorEspecieDTO> obtenerConteoPorEspecie() {
        List<Object[]> resultados = mascotaRepositorio.contarMascotasPorEspecie();

        return resultados.stream()
                .map(resultado -> new ConteoPorEspecieDTO(
                        (String) resultado[0], // Especie
                        (Long) resultado[1]    // Conteo
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<VentasPorServicioDTO> obtenerTopServicios() {
        List<Object[]> resultados = lineaFacturaRepositorio.obtenerTopServiciosVendidos();

        return resultados.stream()
                .map(resultado -> new VentasPorServicioDTO(
                        (String) resultado[0], // Nombre del Servicio
                        (Double) resultado[1]  // Total Vendido
                ))
                .collect(Collectors.toList());
    }
}

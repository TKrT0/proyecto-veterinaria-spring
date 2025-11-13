package com.veterinaria.negocio.servicio;

import com.veterinaria.sistema.entidad.Factura;
import com.veterinaria.sistema.entidad.Pago;

import java.util.List;

public interface IFacturaServicio {
    Factura crearFactura(Factura factura);

    List<Factura> listarFacturasPorCliente(Integer idCliente);

    Factura buscarFacturaPorId(Integer idFactura);

    Factura actualizarEstadoFactura(Integer idFactura, String nuevoEstado);

    Pago registrarPago(Integer idFactura, String metodoPago, Double monto);
}

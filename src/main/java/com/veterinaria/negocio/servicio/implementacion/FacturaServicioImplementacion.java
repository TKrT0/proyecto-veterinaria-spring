package com.veterinaria.negocio.servicio.implementacion;

import com.veterinaria.negocio.servicio.IFacturaServicio;
import com.veterinaria.sistema.entidad.Factura;
import com.veterinaria.sistema.entidad.LineaFactura;
import com.veterinaria.sistema.entidad.Pago;
import com.veterinaria.sistema.repositorio.io.FacturaRepositorio;
import com.veterinaria.sistema.repositorio.io.PagoRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacturaServicioImplementacion implements IFacturaServicio {

    @Autowired
    private FacturaRepositorio facturaRepositorio;
    @Autowired
    private PagoRepositorio pagoRepositorio;

    @Override
    @Transactional
    public Factura crearFactura(Factura factura) {
        factura.setFechaEmision(LocalDateTime.now());
        factura.setEstado("Pendiente");

        double totalCalculado = 0.0;
        for(LineaFactura linea: factura.getLineas()){
            linea.setFactura(factura);

            totalCalculado += linea.getSubtotal();
        }
        factura.setTotal(totalCalculado);

        return facturaRepositorio.save(factura);
    }

    @Override
    public List<Factura> listarFacturasPorCliente(Integer idCliente) {
        return facturaRepositorio.findByClienteId(idCliente);
    }

    @Override
    public Factura buscarFacturaPorId(Integer idFactura) {
        return facturaRepositorio.findById(idFactura).orElse(null);
    }

    @Override
    @Transactional
    public Factura actualizarEstadoFactura(Integer idFactura, String nuevoEstado) {
        Factura factura = buscarFacturaPorId(idFactura);
        if(factura == null){
            return null;
        }
        factura.setEstado(nuevoEstado);
        return facturaRepositorio.save(factura);
    }

    @Override
    @Transactional
    public Pago registrarPago(Integer idFactura, String metodoPago, Double monto) {
        Factura factura = buscarFacturaPorId(idFactura);
        if(factura == null){
            throw new RuntimeException("ERROR: La factura no existe.");
        }
        if(!factura.getEstado().equals("Pendiente")){
            throw new RuntimeException("ERROR: La factura ya está " + factura.getEstado().toLowerCase() + ".");
        }

        Pago nuevoPago = new Pago();
        nuevoPago.setFactura(factura);
        nuevoPago.setFechaPago(LocalDateTime.now());
        nuevoPago.setMetodoPago(metodoPago);
        nuevoPago.setMonto(monto);

        pagoRepositorio.save(nuevoPago);

        factura.setEstado("Pagada");
        facturaRepositorio.save(factura);

        return nuevoPago;
    }
}

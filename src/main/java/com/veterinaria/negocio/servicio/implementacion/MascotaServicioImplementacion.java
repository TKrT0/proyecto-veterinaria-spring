package com.veterinaria.negocio.servicio.implementacion;

import com.veterinaria.negocio.servicio.IMascotaServicio;
import com.veterinaria.negocio.servicio.IClienteServicio;
import com.veterinaria.sistema.entidad.Mascota;
import com.veterinaria.sistema.entidad.Cliente;
import com.veterinaria.sistema.repositorio.io.MascotaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServicioImplementacion implements IMascotaServicio {

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private IClienteServicio clienteServicio;

    @Override
    public Mascota registrarMascota(Mascota mascota, Integer idCliente) {
        Cliente clienteDueno = clienteServicio.buscarClientePorId(idCliente);
        if(clienteDueno != null){
            mascota.setCliente(clienteDueno);
            return mascotaRepositorio.save(mascota);
        }
        return null;
    }

    @Override
    public List<Mascota> listarMascotasPorCliente(Integer idCliente) {
        return mascotaRepositorio.findByClienteId(idCliente);
    }

    @Override
    public Mascota buscarMascotaPorId(Integer idMascota) {
        return mascotaRepositorio.findById(idMascota).orElse(null);
    }

    @Override
    public void eliminarMascota(Integer idMascota) {
        mascotaRepositorio.deleteById(idMascota);
    }

    @Override
    public Mascota actualizarMascota(Mascota mascota) {
        return null;
    }
}

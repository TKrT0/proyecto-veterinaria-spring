package com.veterinaria.negocio.servicio;

import com.veterinaria.sistema.entidad.Mascota;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMascotaServicio {
    // Registro de mascotas y la asocia a un cliente existente

    /**
     *
     * @param mascota mascota a registrar
     * @param idCliente id del cliente al que se le asocia la mascota
     * @return la mascota registrada con su nuevo id***/
    Mascota registrarMascota(Mascota mascota, Integer idCliente);
    /**
     *
     * @param idCliente id del cliente al que se le asocia la mascota
     * @return la mascota registrada con su nuevo id*/
    List<Mascota> listarMascotasPorCliente(Integer idCliente);

    Mascota buscarMascotaPorId(Integer idMascota);

    void eliminarMascota(Integer idMascota);

    Mascota actualizarMascota(Mascota mascota);

}

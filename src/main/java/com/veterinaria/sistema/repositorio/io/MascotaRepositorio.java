package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MascotaRepositorio extends JpaRepository<Mascota, Integer> {
    List<Mascota> findByClienteId(Integer id);

    @Query("SELECT m.especie, COUNT(m) FROM Mascota m GROUP BY m.especie")
    List<Object[]> contarMascotasPorEspecie();
}

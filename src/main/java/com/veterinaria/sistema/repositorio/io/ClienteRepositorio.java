package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    @Query("SELECT COUNT(c) FROM Cliente c")
    Long contarTotalClientes();
}

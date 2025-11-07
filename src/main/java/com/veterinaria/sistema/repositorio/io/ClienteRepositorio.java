package com.veterinaria.sistema.repositorio.io;

import com.veterinaria.sistema.entidad.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
}

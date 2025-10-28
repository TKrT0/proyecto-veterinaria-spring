package com.veterinaria.negocio.servicio;

import com.veterinaria.sistema.entidad.Cliente;

import java.util.List;

public interface IClienteServicio {
    // Registro de clientes
    Cliente registrarCliente(Cliente cliente);

    // Listar clientes
    List<Cliente> listarClientes();

    // Buscar cliente por id
    Cliente buscarClientePorId(Integer id);

    // Eliminar cliente
    void eliminarCliente(Integer id);
}
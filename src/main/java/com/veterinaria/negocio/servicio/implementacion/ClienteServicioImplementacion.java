package com.veterinaria.negocio.servicio.implementacion;

import com.veterinaria.negocio.servicio.IClienteServicio;
import com.veterinaria.sistema.entidad.Cliente;
import com.veterinaria.sistema.repositorio.io.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServicioImplementacion implements IClienteServicio {
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Override
    public Cliente registrarCliente(Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    @Override
    public List<Cliente> listarClientes(){
        List<Cliente> clientes = clienteRepositorio.findAll();
        return clientes;
    }

    @Override
    public Cliente buscarClientePorId(Integer id) {
        Cliente cliente = clienteRepositorio.findById(id).orElse(null);
        return cliente;
    }

    @Override
    public void eliminarCliente(Integer id) {
        clienteRepositorio.deleteById(id);
    }
}

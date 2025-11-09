package com.veterinaria.ui.cliente;

import com.veterinaria.negocio.servicio.ICitaServicio;
import com.veterinaria.negocio.servicio.IClienteServicio;
import com.veterinaria.negocio.servicio.IHistorialClinicoServicio;
import com.veterinaria.negocio.servicio.IMascotaServicio;
import com.veterinaria.sistema.entidad.Cliente;
import com.veterinaria.sistema.entidad.HistorialClinico;
import com.veterinaria.sistema.entidad.Mascota;
import com.veterinaria.ui.cita.CitaForm;
import com.veterinaria.ui.historial.HistorialCitasForm;
import com.veterinaria.ui.historial_clinico.HistorialClinicoForm;
import com.veterinaria.ui.mascota.MascotaForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteForm extends JFrame {
    // Validación de campos en TextField (email, teléfono, etc)
    private static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String TEL_REGEX =
            "^[0-9]{10}$";
    private JTextField nombreTexto;
    private JTextField apellidoTexto;
    private JTextField telefonoTexto;
    private JTextField emailTexto;
    private JTextField direccionTexto;
    private JPanel panelPrincipal;
    private JTable clientesTabla;
    private JButton guardarButton;
    private JButton agregarMascotaButton;
    private JPanel panelTabla;
    private JPanel panelCampos;
    private JPanel panelBotones;
    private JScrollPane mascotasScrollPane;
    private JScrollPane clientesScrollPane;
    private JTable mascotasTabla;
    private JPanel panelTablaMascotas;
    private JButton programarCitaButton;
    private JButton historialButton;
    private JButton verHistorialButton;
    private DefaultTableModel tablaModeloClientes;
    private DefaultTableModel tablaModeloMascotas;

    private IClienteServicio clienteServicio;
    private IMascotaServicio mascotaServicio;
    private ICitaServicio citaServicio;
    private HistorialCitasForm historialCitasForm;
    private IHistorialClinicoServicio historialServicio;

    @Autowired
    public ClienteForm(IClienteServicio clienteServicio,
                       IMascotaServicio mascotaServicio,
                       ICitaServicio citaServicio,
                       HistorialCitasForm historialCitasForm,
                       IHistorialClinicoServicio historialServicio) {

        this.clienteServicio = clienteServicio;
        this.mascotaServicio = mascotaServicio;
        this.citaServicio = citaServicio;
        this.historialCitasForm = historialCitasForm;
        this.historialServicio = historialServicio;

        iniciarForma();

        historialButton.addActionListener(e -> abrirHistorial());

        programarCitaButton.addActionListener(e -> abrirDialogoCita());
        guardarButton.addActionListener(e -> registrarCliente());
        agregarMascotaButton.addActionListener(e -> abrirDialogoMascota());
        listarClientes();
        clientesTabla.getSelectionModel().addListSelectionListener(e -> {
           if(!e.getValueIsAdjusting()){
               int filaSeleccionada = clientesTabla.getSelectedRow();

               if(filaSeleccionada != -1){
                   Integer idCliente = (Integer) tablaModeloClientes.getValueAt(filaSeleccionada, 0);
                   listarMascotas(idCliente);
               }
               else {
                   tablaModeloMascotas.setRowCount(0);
               }
           }
        });
        verHistorialButton.addActionListener(e -> abrirDialogoHistorialClinico());
    }

    private void abrirDialogoHistorialClinico() {
        int filaMascota = mascotasTabla.getSelectedRow();
        if(filaMascota == -1){
            mostrarMensaje("Debe seleccionar una MASCOTA para ver su historial", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer idMascota = (Integer) tablaModeloMascotas.getValueAt(filaMascota, 0);

        HistorialClinicoForm historialForm = new HistorialClinicoForm(this, this.historialServicio, idMascota);
        historialForm.setVisible(true);
    }

    private void abrirHistorial() {
        // Carga los datos frescos de la BD
        historialCitasForm.cargarDatos();
        // Muestra la ventana
        historialCitasForm.setVisible(true);
    }

    private void abrirDialogoCita() {
        // Primero, verifica que una mascota esté seleccionada
        int filaMascota = mascotasTabla.getSelectedRow();
        if (filaMascota == -1) {
            mostrarMensaje("Debe seleccionar una MASCOTA para programar una cita", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtiene el ID de la mascota de la tabla de mascotas
        Integer idMascota = (Integer) tablaModeloMascotas.getValueAt(filaMascota, 0);

        CitaForm citaForm = new CitaForm(this, this.citaServicio, idMascota);
        citaForm.setVisible(true);
    }

    /**
     * Lista las mascotas registradas del cliente seleccionado
     * @param idCliente
     */

    private void listarMascotas(Integer idCliente) {
        this.tablaModeloMascotas.setRowCount(0);
        List<Mascota> mascotas = mascotaServicio.listarMascotasPorCliente(idCliente);
        System.out.println("Mascotas encontradas para ID " + idCliente + ": " + mascotas.size());

        mascotas.forEach(mascota -> {
            Object[] renglonMascota = {
                mascota.getIdMascota(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza()
            };
            this.tablaModeloMascotas.addRow(renglonMascota);
        });
    }

    private void abrirDialogoMascota() {
        int filaSeleccionada = clientesTabla.getSelectedRow();
        if(filaSeleccionada == -1){
            mostrarMensaje("Debe seleccionar un cliente para agregar una mascota", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer idCliente = (Integer) tablaModeloClientes.getValueAt(filaSeleccionada, 0);
        MascotaForm mascotaForm = new MascotaForm(this, this.mascotaServicio, idCliente);
        mascotaForm.setVisible(true);
        listarMascotas(idCliente);
    }

    private void registrarCliente() {
        String nombre = nombreTexto.getText();
        String apellido = apellidoTexto.getText();
        String telefono = telefonoTexto.getText();
        String email = emailTexto.getText();
        String direccion = direccionTexto.getText();

        if(!validarCampos()){
            return;
        }

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(nombre);
        nuevoCliente.setApellido(apellido);
        nuevoCliente.setTelefono(telefono);
        nuevoCliente.setEmail(email);
        nuevoCliente.setDireccion(direccion);

        try{
            Cliente clienteGuardado = clienteServicio.registrarCliente(nuevoCliente);
            mostrarMensaje("Cliente registrado con éxito, ID: " + clienteGuardado.getId(), null, JOptionPane.INFORMATION_MESSAGE);
        }catch(Exception ex){
            mostrarMensaje("Error al registrar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
        }
        limpiarFormulario();
        listarClientes();
    }

    private boolean validarCampos() {
        List<String> errores = new ArrayList<>();
        String nombre = nombreTexto.getText().trim();
        String apellido = apellidoTexto.getText().trim();
        String telefono = telefonoTexto.getText().trim();
        String email = emailTexto.getText().trim();

        if(nombre.isEmpty()){
            errores.add("El nombre es obligatorio");
        }
        if(apellido.isEmpty()){
            errores.add("El apellido es obligatorio");
        }
        if(telefono.isEmpty()){
            errores.add("El teléfono es obligatorio");
        }
        if(email.isEmpty()){
            errores.add("El email es obligatorio");
        }
        // Formato de celuco
        if(!telefono.isEmpty() && !telefono.matches(TEL_REGEX)){
            errores.add("El teléfono debe de tener 10 digitos (sin espacios ni letras");
        }
        // Formato de email
        if(!email.isEmpty() && !email.matches(EMAIL_REGEX)){
            errores.add("El formato del email es incorrecto");
        }
        if (errores.isEmpty()) {
            return true;
        }
        else {
            String mensajeError = String.join("\n", errores);
            mostrarMensaje(mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void limpiarFormulario() {
        nombreTexto.setText("");
        apellidoTexto.setText("");
        telefonoTexto.setText("");
        emailTexto.setText("");
        direccionTexto.setText("");
    }

    private void mostrarMensaje(String mensaje, String error, int mensajeError) {
        JOptionPane.showMessageDialog(this, mensaje, error, mensajeError);
    }

    private void iniciarForma() {
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        this.tablaModeloClientes = new DefaultTableModel(0, 5){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] columnas = {"ID", "Nombre", "Apellido", "Telefono", "Email"};
        this.tablaModeloClientes.setColumnIdentifiers(columnas);
        this.clientesTabla = new JTable(tablaModeloClientes);
        this.clientesTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tablaModeloMascotas = new DefaultTableModel(0,4){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] columnasMascotas = {"ID", "Nombre", "Especie", "Raza"};
        this.tablaModeloMascotas.setColumnIdentifiers(columnasMascotas);
        this.mascotasTabla = new JTable(tablaModeloMascotas);
        this.mascotasTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void listarClientes() {
        this.tablaModeloClientes.setRowCount(0);
        List<Cliente> clientes = clienteServicio.listarClientes();
        clientes.forEach(cliente ->{
        Object[] renglonCliente = {
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getEmail(),
                };
            this.tablaModeloClientes.addRow(renglonCliente);
        });
    }
}

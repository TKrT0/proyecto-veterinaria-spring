package com.veterinaria.ui.historial;

import com.veterinaria.negocio.servicio.ICitaServicio;
import com.veterinaria.sistema.entidad.Cita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HistorialCitasForm extends JFrame {
    private JPanel panelPrincipal;
    private JTable tablaHistorial;
    private JButton marcarCompletadaButton;
    private JButton marcarPendienteButton;
    private JButton refrescarButton;
    private JScrollPane scrollPane;
    private DefaultTableModel tablaModeloHistorial;

    private ICitaServicio citaServicio;

    @Autowired
    public HistorialCitasForm(ICitaServicio citaServicio) {
        this.citaServicio = citaServicio;
        iniciarForma();

        // Acción para los botones
        refrescarButton.addActionListener(e -> cargarDatos());

        marcarCompletadaButton.addActionListener(e -> actualizarEstado("Completada"));

        marcarPendienteButton.addActionListener(e -> actualizarEstado("Pendiente"));
    }

    private void iniciarForma() {
        setContentPane(panelPrincipal);
        setTitle("Historial de Citas");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Ocultar en lugar de cerrar
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrar
    }

    // Método público para que ClienteForm pueda llamarlo
    public void cargarDatos() {
        this.tablaModeloHistorial.setRowCount(0); // Limpiar tabla
        List<Cita> citas = citaServicio.listarTodasLasCitas();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        citas.forEach(cita -> {
            Object[] renglonCita = {
                    cita.getIdCita(),
                    cita.getMascota().getCliente().getNombre(), // Dueño
                    cita.getMascota().getNombre(), // Mascota
                    cita.getFechaHora().format(formatter), // Fecha y Hora
                    cita.getMotivo(),
                    cita.getEstado() // Estado
            };
            this.tablaModeloHistorial.addRow(renglonCita);
        });
    }

    private void actualizarEstado(String nuevoEstado) {
        int filaSeleccionada = tablaHistorial.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarMensaje("Debe seleccionar una cita de la tabla", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Columna 0 es el ID de la cita
            Integer idCita = (Integer) tablaModeloHistorial.getValueAt(filaSeleccionada, 0);

            // Llamamos al servicio que ya teníamos
            citaServicio.actualizarEstadoCita(idCita, nuevoEstado);

            mostrarMensaje("Estado de la cita actualizado con éxito", null, JOptionPane.INFORMATION_MESSAGE);

            // Recargamos los datos para ver el cambio
            cargarDatos();

        } catch (Exception e) {
            mostrarMensaje("Error al actualizar el estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    private void createUIComponents() {
        // Configuración de la tabla (igual que en ClienteForm)
        this.tablaModeloHistorial = new DefaultTableModel(0, 6) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Que no se pueda editar
            }
        };
        String[] columnas = {"ID", "Dueño", "Mascota", "Fecha/Hora", "Motivo", "Estado"};
        this.tablaModeloHistorial.setColumnIdentifiers(columnas);

        this.tablaHistorial = new JTable(tablaModeloHistorial);
        this.tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
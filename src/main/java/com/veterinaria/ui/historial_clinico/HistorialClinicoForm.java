package com.veterinaria.ui.historial_clinico;

import com.veterinaria.negocio.servicio.IHistorialClinicoServicio;
import com.veterinaria.sistema.entidad.HistorialClinico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialClinicoForm extends JDialog{
    private JPanel panelPrincipal;
    private JTable tablaHistorial;
    private JTextArea diagnosticoTexto;
    private JTextArea tratamientoTexto;
    private JTextArea notasTexto;
    private JButton guardarEntradaButton;
    private DefaultTableModel tablaModeloHistorial;

    private IHistorialClinicoServicio historialServicio;
    private Integer idMascota;

    public HistorialClinicoForm(JFrame owner, IHistorialClinicoServicio historialServicio, Integer idMascota) {
        super(owner, "Historial clinico", true);

        this.historialServicio = historialServicio;
        this.idMascota = idMascota;

        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(owner);

        guardarEntradaButton.addActionListener(e -> registrarNuevaEntrada());
        cargarHistorial();
    }

    private void cargarHistorial() {
        this.tablaModeloHistorial.setRowCount(0);

        List<HistorialClinico> historial = historialServicio.listarHistorialPorMascota(this.idMascota);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        historial.forEach(entrada -> {
           Object[] fila = {
                   entrada.getFechaVisita().format(formatter),
                   entrada.getDiagnostico(),
                   entrada.getTratamiento(),
                   entrada.getNotas()
           };
           this.tablaModeloHistorial.addRow(fila);
        });
    }

    private void registrarNuevaEntrada() {
        String diagnostico = this.diagnosticoTexto.getText().trim();
        String tratamiento = this.tratamientoTexto.getText().trim();
        String notas = this.notasTexto.getText().trim();

        if(diagnostico.isEmpty() && tratamiento.isEmpty() && notas.isEmpty()){
            mostrarMensaje("Debe rellenar al menos un campo (diagnóstico, tratamiento o notas)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        HistorialClinico nuevaEntrada = new HistorialClinico();
        nuevaEntrada.setDiagnostico(diagnostico);
        nuevaEntrada.setTratamiento(tratamiento);
        nuevaEntrada.setNotas(notas);

        try{
            historialServicio.agregarEntradaHistorial(nuevaEntrada, this.idMascota);
            mostrarMensaje("Nueva entrada de historial clinico registrada", null, JOptionPane.INFORMATION_MESSAGE);
            cargarHistorial();
            limpiarCampos();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error al registrar la entrada de historial clinico", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        this.diagnosticoTexto.setText("");
        this.tratamientoTexto.setText("");
        this.notasTexto.setText("");
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        this.tablaModeloHistorial = new DefaultTableModel(0, 4){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] columnas = {"Fecha visita", "Diagnostico", "Tratamiento", "Notas"};
        this.tablaModeloHistorial.setColumnIdentifiers(columnas);
        this.tablaHistorial = new JTable(tablaModeloHistorial);
        this.tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void mostrarMensaje(String mensaje, String error, int mensajeError) {
        JOptionPane.showMessageDialog(this, mensaje, error, mensajeError);
    }
}

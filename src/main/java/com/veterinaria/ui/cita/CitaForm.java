package com.veterinaria.ui.cita;

import com.toedter.calendar.JDateChooser;
import com.veterinaria.negocio.servicio.ICitaServicio;
import com.veterinaria.sistema.entidad.Cita;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class CitaForm extends JDialog {
    private JPanel panelPrincipal;
    private JPanel panelHora;
    private JTextField motivoTexto;
    private JButton guardarCitaButton;
    private JPanel panelFecha;
    private JDateChooser dateChooserControl;
    private JSpinner horaSpinnerControl;


    private ICitaServicio citaServicio;
    private Integer idMascota;

    public CitaForm(JFrame owner, ICitaServicio citaServicio, Integer idMascota) {
        super(owner, "Programar Cita", true);
        this.citaServicio = citaServicio;
        this.idMascota = idMascota;

        iniciarForma(owner); // Esto infla el formulario (panelFecha, panelHora, etc.)

        // !! AÑADE ESTA LLAMADA !!
        inicializarComponentesCustom();

        guardarCitaButton.addActionListener(e -> registrarCita());
    }
    private void inicializarComponentesCustom() {
        // 1. Crear el JDateChooser
        this.dateChooserControl = new JDateChooser();
        this.dateChooserControl.setDateFormatString("yyyy-MM-dd");

        // 2. Crear el JSpinner (con el modelo de horas)
        String[] horasHabiles = {
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00"
        };
        SpinnerListModel model = new SpinnerListModel(horasHabiles);
        this.horaSpinnerControl = new JSpinner(model);

        // 3. Añadir los componentes a los paneles (usando BorderLayout)
        this.panelFecha.add(this.dateChooserControl, java.awt.BorderLayout.CENTER);
        this.panelHora.add(this.horaSpinnerControl, java.awt.BorderLayout.CENTER);

        // Forzar a los paneles a re-dibujarse con el nuevo componente
        this.panelFecha.revalidate();
        this.panelFecha.repaint();
        this.panelHora.revalidate();
        this.panelHora.repaint();
    }

    private void registrarCita() {
        // 1. Obtener los datos del formulario
        Date fechaSeleccionada = dateChooserControl.getDate();
        String horaSeleccionada = (String) horaSpinnerControl.getValue();
        String motivo = motivoTexto.getText().trim();

        // 2. Validación simple
        if (fechaSeleccionada == null || motivo.isEmpty()) {
            mostrarMensaje("La fecha y el motivo son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 3. Convertir los datos al formato LocalDateTime (como en la Entidad Cita)

            // Convertir java.util.Date (del JDateChooser) a LocalDate
            LocalDate localDate = fechaSeleccionada.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Convertir String (del JSpinner) a LocalTime
            LocalTime localTime = LocalTime.parse(horaSeleccionada);

            // Combinar ambos
            LocalDateTime fechaHoraCita = LocalDateTime.of(localDate, localTime);

            // Validar que la cita no sea en el pasado (opcional pero recomendado)
            if(fechaHoraCita.isBefore(LocalDateTime.now())){
                mostrarMensaje("No se pueden programar citas en fechas pasadas.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 4. Crear la entidad
            Cita nuevaCita = new Cita();
            nuevaCita.setFechaHora(fechaHoraCita);
            nuevaCita.setMotivo(motivo);
            // El estado y la mascota se asignan en el servicio

            // 5. Guardar usando el servicio
            Cita citaGuardada = citaServicio.programarCita(nuevaCita, this.idMascota);

            if (citaGuardada != null) {
                mostrarMensaje("Cita programada con éxito para la mascota ID: " + this.idMascota, null, JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Cerrar la ventana al guardar
            } else {
                mostrarMensaje("Error al programar la cita.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException ex) {
            mostrarMensaje("Error en el formato de la hora.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            mostrarMensaje("Ocurrió un error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    private void iniciarForma(JFrame owner) {
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 300); // Tamaño ajustado
        setLocationRelativeTo(owner);
    }

}


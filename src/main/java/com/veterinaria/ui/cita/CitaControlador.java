package com.veterinaria.ui.cita;

import com.veterinaria.negocio.servicio.ICitaServicio;
import com.veterinaria.sistema.entidad.Cita;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class CitaControlador {

    @Autowired
    private ICitaServicio citaServicio;

    @FXML
    private DatePicker fechaPicker;
    @FXML
    private ComboBox<String> horaCombo;
    @FXML
    private TextField motivoTexto;
    @FXML
    private Button guardarCitaButton;

    private Integer idMascota;

    @FXML
    public void initialize() {
        String[] horasHabiles = {
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00"
        };
        horaCombo.setItems(FXCollections.observableArrayList(horasHabiles));
    }

    public void iniciarDatos(Integer idMascota) {
        this.idMascota = idMascota;
    }

    @FXML
    private void registrarCita() {
        LocalDate fechaSeleccionada = fechaPicker.getValue();
        String horaSeleccionada = horaCombo.getValue();
        String motivo = motivoTexto.getText().trim();

        if (fechaSeleccionada == null || horaSeleccionada == null || horaSeleccionada.isEmpty() || motivo.isEmpty()) {
            mostrarMensaje("La fecha, la hora y el motivo son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        if (this.idMascota == null) {
            mostrarMensaje("Error: No se pudo identificar la mascota. Cierre esta ventana e inténtelo de nuevo.", Alert.AlertType.ERROR);
            return;
        }

        try {
            LocalTime localTime = LocalTime.parse(horaSeleccionada);
            LocalDateTime fechaHoraCita = LocalDateTime.of(fechaSeleccionada, localTime);

            if (fechaHoraCita.isBefore(LocalDateTime.now())) {
                mostrarMensaje("No se pueden programar citas en fechas pasadas.", Alert.AlertType.WARNING);
                return;
            }

            Cita nuevaCita = new Cita();
            nuevaCita.setFechaHora(fechaHoraCita);
            nuevaCita.setMotivo(motivo);

            Cita citaGuardada = citaServicio.programarCita(nuevaCita, this.idMascota);

            if (citaGuardada != null) {
                mostrarMensaje("Cita programada con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarMensaje("Error al programar la cita (no se encontró la mascota).", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            mostrarMensaje("Error inesperado al registrar la cita: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) guardarCitaButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
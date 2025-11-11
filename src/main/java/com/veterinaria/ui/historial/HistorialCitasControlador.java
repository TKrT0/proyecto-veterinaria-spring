package com.veterinaria.ui.historial;

import com.veterinaria.negocio.servicio.ICitaServicio;
import com.veterinaria.sistema.entidad.Cita;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HistorialCitasControlador {

    @Autowired
    private ICitaServicio citaServicio;

    @FXML
    private TableView<Cita> tablaHistorial;
    @FXML
    private TableColumn<Cita, Integer> colIdCita;
    @FXML
    private TableColumn<Cita, String> colDueno;
    @FXML
    private TableColumn<Cita, String> colMascota;
    @FXML
    private TableColumn<Cita, LocalDateTime> colFecha;
    @FXML
    private TableColumn<Cita, String> colMotivo;
    @FXML
    private TableColumn<Cita, String> colEstado;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        colIdCita.setCellValueFactory(new PropertyValueFactory<>("idCita"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colMascota.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMascota().getNombre())
        );
        colDueno.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMascota().getCliente().getNombre())
        );

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colFecha.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        cargarDatos();
    }

    @FXML
    public void cargarDatos() {
        List<Cita> citas = citaServicio.listarTodasLasCitas();
        tablaHistorial.setItems(FXCollections.observableArrayList(citas));
    }

    @FXML
    private void actualizarEstadoPendiente() {
        actualizarEstado("Pendiente");
    }

    @FXML
    private void actualizarEstadoCompletada() {
        actualizarEstado("Completada");
    }

    private void actualizarEstado(String nuevoEstado) {
        Cita citaSeleccionada = tablaHistorial.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarMensaje("Debe seleccionar una cita de la tabla", Alert.AlertType.WARNING);
            return;
        }

        try {
            citaServicio.actualizarEstadoCita(citaSeleccionada.getIdCita(), nuevoEstado);
            mostrarMensaje("Estado de la cita actualizado con éxito", Alert.AlertType.INFORMATION);

            cargarDatos();

        } catch (Exception e) {
            mostrarMensaje("Error al actualizar el estado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
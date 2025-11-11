package com.veterinaria.ui.historial_clinico;

import com.veterinaria.negocio.servicio.IHistorialClinicoServicio;
import com.veterinaria.sistema.entidad.HistorialClinico;
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
public class HistorialClinicoControlador {

    @Autowired
    private IHistorialClinicoServicio historialServicio;

    @FXML
    private TableView<HistorialClinico> tablaHistorial;
    @FXML
    private TableColumn<HistorialClinico, LocalDateTime> colFecha;
    @FXML
    private TableColumn<HistorialClinico, String> colDiagnostico;
    @FXML
    private TableColumn<HistorialClinico, String> colTratamiento;
    @FXML
    private TableColumn<HistorialClinico, String> colNotas;

    @FXML
    private TextArea diagnosticoTexto;
    @FXML
    private TextArea tratamientoTexto;
    @FXML
    private TextArea notasTexto;

    private Integer idMascota;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void initData(Integer idMascota) {
        this.idMascota = idMascota;
        cargarHistorial();
    }

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaVisita"));
        colDiagnostico.setCellValueFactory(new PropertyValueFactory<>("diagnostico"));
        colTratamiento.setCellValueFactory(new PropertyValueFactory<>("tratamiento"));
        colNotas.setCellValueFactory(new PropertyValueFactory<>("notas"));

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
    }

    private void cargarHistorial() {
        if (this.idMascota != null) {
            List<HistorialClinico> historial = historialServicio.listarHistorialPorMascota(this.idMascota);
            tablaHistorial.setItems(FXCollections.observableArrayList(historial));
        }
    }

    @FXML
    private void registrarNuevaEntrada() {
        String diagnostico = diagnosticoTexto.getText().trim();
        String tratamiento = tratamientoTexto.getText().trim();
        String notas = notasTexto.getText().trim();

        if (diagnostico.isEmpty() && tratamiento.isEmpty() && notas.isEmpty()) {
            mostrarMensaje("Debe rellenar al menos un campo (diagnóstico, tratamiento o notas)", Alert.AlertType.WARNING);
            return;
        }

        if (this.idMascota == null) {
            mostrarMensaje("Error: No se pudo identificar la mascota.", Alert.AlertType.ERROR);
            return;
        }

        HistorialClinico nuevaEntrada = new HistorialClinico();
        nuevaEntrada.setDiagnostico(diagnostico);
        nuevaEntrada.setTratamiento(tratamiento);
        nuevaEntrada.setNotas(notas);

        try {
            historialServicio.agregarEntradaHistorial(nuevaEntrada, this.idMascota);

            mostrarMensaje("Nueva entrada de historial registrada con éxito.", Alert.AlertType.INFORMATION);

            cargarHistorial();
            limpiarCampos();

        } catch (Exception e) {
            mostrarMensaje("Error al registrar la entrada: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        diagnosticoTexto.clear();
        tratamientoTexto.clear();
        notasTexto.clear();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
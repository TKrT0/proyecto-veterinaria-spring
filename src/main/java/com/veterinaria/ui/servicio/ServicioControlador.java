package com.veterinaria.ui.servicio;

import com.veterinaria.negocio.servicio.IServicioServicio;
import com.veterinaria.sistema.entidad.Servicio;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ServicioControlador {

    @Autowired
    private IServicioServicio servicioServicio;

    @FXML
    private Label tituloFormulario;
    @FXML
    private MFXTextField nombreTexto;
    @FXML
    private MFXTextField descripcionTexto;
    @FXML
    private MFXTextField precioTexto;
    @FXML
    private TableView<Servicio> tablaServicios;
    @FXML
    private TableColumn<Servicio, Integer> colId;
    @FXML
    private TableColumn<Servicio, String> colNombre;
    @FXML
    private TableColumn<Servicio, Double> colPrecio;

    private Servicio servicioSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        cargarServicios();

        tablaServicios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        this.servicioSeleccionado = newSelection;
                        cargarDatosEnFormulario(newSelection);
                    }
                }
        );
    }

    @FXML
    private void guardarServicio() {

        if (nombreTexto.getText().isEmpty() || precioTexto.getText().isEmpty()) {
            mostrarMensaje("El Nombre y el Precio son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto.getText());

            if (this.servicioSeleccionado == null) {
                this.servicioSeleccionado = new Servicio();
            }

            this.servicioSeleccionado.setNombre(nombreTexto.getText());
            this.servicioSeleccionado.setDescripcion(descripcionTexto.getText());
            this.servicioSeleccionado.setPrecio(precio);

            servicioServicio.guardarServicio(this.servicioSeleccionado);

            mostrarMensaje("Servicio guardado con éxito.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarServicios();

        } catch (NumberFormatException e) {
            mostrarMensaje("El precio debe ser un número válido (ej: 500.0).", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarMensaje("Error al guardar el servicio: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarServicio() {
        if (this.servicioSeleccionado == null) {
            mostrarMensaje("Debe seleccionar un servicio de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar el servicio?");
        confirmacion.setContentText(this.servicioSeleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                servicioServicio.eliminarServicio(this.servicioSeleccionado.getIdServicio());
                mostrarMensaje("Servicio eliminado con éxito.", Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarServicios();
            } catch (Exception e) {
                mostrarMensaje("Error al eliminar el servicio. Es posible que esté siendo usado en una factura.", Alert.AlertType.ERROR);
            }
        }
    }

    private void cargarServicios() {
        List<Servicio> servicios = servicioServicio.listarServicios();
        tablaServicios.setItems(FXCollections.observableArrayList(servicios));
    }

    private void cargarDatosEnFormulario(Servicio servicio) {
        tituloFormulario.setText("Editar Servicio (ID: " + servicio.getIdServicio() + ")");
        nombreTexto.setText(servicio.getNombre());
        descripcionTexto.setText(servicio.getDescripcion());
        precioTexto.setText(String.valueOf(servicio.getPrecio()));
    }

    @FXML
    private void limpiarCampos() {
        this.servicioSeleccionado = null;
        tituloFormulario.setText("Agregar Servicio");
        nombreTexto.clear();
        descripcionTexto.clear();
        precioTexto.clear();
        tablaServicios.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
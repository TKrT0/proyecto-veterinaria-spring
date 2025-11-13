package com.veterinaria.ui.facturacion;

import com.veterinaria.negocio.servicio.IFacturaServicio;
import com.veterinaria.negocio.servicio.IServicioServicio;
import com.veterinaria.sistema.entidad.Cliente;
import com.veterinaria.sistema.entidad.Factura;
import com.veterinaria.sistema.entidad.LineaFactura;
import com.veterinaria.sistema.entidad.Servicio;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class FacturacionControlador {

    @Autowired
    private IFacturaServicio facturaServicio;
    @Autowired
    private IServicioServicio servicioServicio;

    @FXML
    private Label infoClienteLabel;
    @FXML
    private MFXComboBox<Servicio> servicioCombo;
    @FXML
    private MFXTextField cantidadTexto;
    @FXML
    private TableView<LineaFactura> tablaLineas;
    @FXML
    private TableColumn<LineaFactura, String> colServicio;
    @FXML
    private TableColumn<LineaFactura, Integer> colCantidad;
    @FXML
    private TableColumn<LineaFactura, Double> colPrecioUnit;
    @FXML
    private TableColumn<LineaFactura, Double> colSubtotal;
    @FXML
    private Label totalLabel;
    @FXML
    private MFXButton guardarFacturaButton;
    @FXML
    private MFXButton quitarButton;

    private Cliente clienteFacturar;
    private ObservableList<LineaFactura> lineasFacturaList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colServicio.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getServicio().getNombre())
        );
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnit.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tablaLineas.setItems(lineasFacturaList);
        cargarServicios();
    }

    public void initData(Cliente cliente) {
        this.clienteFacturar = cliente;
        infoClienteLabel.setText("Cliente: " + cliente.getNombre() + " " + cliente.getApellido() + " (ID: " + cliente.getId() + ")");
    }

    private void cargarServicios() {
        servicioCombo.setItems(FXCollections.observableArrayList(servicioServicio.listarServicios()));

        servicioCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Servicio servicio) {
                return servicio == null ? null : servicio.getNombre() + " ($" + servicio.getPrecio() + ")";
            }
            @Override
            public Servicio fromString(String string) { return null; }
        });
    }

    @FXML
    private void aniadirItem() {
        Servicio servicioSel = servicioCombo.getValue();
        String cantidadStr = cantidadTexto.getText();

        if (servicioSel == null || cantidadStr.isEmpty()) {
            mostrarMensaje("Debe seleccionar un servicio e indicar la cantidad.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                mostrarMensaje("La cantidad debe ser mayor a cero.", Alert.AlertType.WARNING);
                return;
            }

            LineaFactura nuevaLinea = new LineaFactura();
            nuevaLinea.setServicio(servicioSel);
            nuevaLinea.setCantidad(cantidad);
            nuevaLinea.setPrecioUnitario(servicioSel.getPrecio());
            nuevaLinea.setSubtotal(servicioSel.getPrecio() * cantidad);

            lineasFacturaList.add(nuevaLinea);

            recalcularTotal();

            servicioCombo.clearSelection();
            cantidadTexto.clear();

        } catch (NumberFormatException e) {
            mostrarMensaje("La cantidad debe ser un número entero válido.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void quitarItem() {
        LineaFactura lineaSel = tablaLineas.getSelectionModel().getSelectedItem();
        if (lineaSel == null) {
            mostrarMensaje("Seleccione una línea de la tabla para quitar.", Alert.AlertType.WARNING);
            return;
        }
        lineasFacturaList.remove(lineaSel);
        recalcularTotal();
    }

    private void recalcularTotal() {
        double total = 0.0;
        for (LineaFactura linea : lineasFacturaList) {
            total += linea.getSubtotal();
        }
        totalLabel.setText(String.format("$ %.2f", total));
    }

    @FXML
    private void guardarFactura() {
        if (lineasFacturaList.isEmpty()) {
            mostrarMensaje("No puede guardar una factura vacía. Añada al menos un servicio.", Alert.AlertType.WARNING);
            return;
        }

        if (this.clienteFacturar == null) {
            mostrarMensaje("Error fatal: No hay un cliente asociado. Cierre y reintente.", Alert.AlertType.ERROR);
            return;
        }

        Factura nuevaFactura = new Factura();
        nuevaFactura.setCliente(this.clienteFacturar);
        nuevaFactura.setLineas(new ArrayList<>(lineasFacturaList));

        try {
            Factura facturaGuardada = facturaServicio.crearFactura(nuevaFactura);

            mostrarMensaje("Factura #" + facturaGuardada.getIdFactura() + " guardada con éxito con estado 'Pendiente'.", Alert.AlertType.INFORMATION);

            Stage stage = (Stage) guardarFacturaButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            mostrarMensaje("Error al guardar la factura: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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
package com.veterinaria.ui.facturacion;

import com.veterinaria.negocio.reporte.ReporteServicio;
import com.veterinaria.negocio.servicio.IFacturaServicio;
import com.veterinaria.sistema.entidad.Cliente;
import com.veterinaria.sistema.entidad.Factura;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ClienteFacturasControlador {

    @Autowired
    private IFacturaServicio facturaServicio;
    
    @Autowired
    private ReporteServicio reporteServicio;
    
    @Autowired
    private ConfigurableApplicationContext contextoSpring;

    @FXML
    private Label infoClienteLabel;
    @FXML
    private TableView<Factura> tablaFacturas;
    @FXML
    private TableColumn<Factura, Integer> colIdFactura;
    @FXML
    private TableColumn<Factura, LocalDateTime> colFecha;
    @FXML
    private TableColumn<Factura, Double> colTotal;
    @FXML
    private TableColumn<Factura, String> colEstado;
    @FXML
    private MFXButton pagarButton;
    @FXML
    private MFXButton pdfButton;

    private Cliente cliente;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void initData(Cliente cliente) {
        this.cliente = cliente;
        infoClienteLabel.setText("Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
        cargarFacturas();
    }

    @FXML
    public void initialize() {
        colIdFactura.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaEmision"));
        colFecha.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
                tablaFacturas.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldSel, newSel) -> {
                            if (newSel != null) {
                                pagarButton.setDisable(!newSel.getEstado().equals("Pendiente"));
                                pdfButton.setDisable(false);
                            } else {
                                pagarButton.setDisable(true);
                                pdfButton.setDisable(true);
                            }
                        }
                );
            }
        });

        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                getStyleClass().removeAll("status-success", "status-error"); 
                if (item != null) {
                    if (item.equals("Pagada")) {
                        getStyleClass().add("status-success");
                    } else if (item.equals("Pendiente")) {
                        getStyleClass().add("status-error");
                    }
                }
            }
        });

        // 6. Listener para habilitar/deshabilitar el botón de Pagar
        tablaFacturas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null && newSel.getEstado().equals("Pendiente")) {
                        pagarButton.setDisable(false);
                    } else {
                        pagarButton.setDisable(true);
                    }
                }
        );
    }

    @FXML
    public void cargarFacturas() {
        if (this.cliente != null) {
            List<Factura> facturas = facturaServicio.listarFacturasPorCliente(this.cliente.getId());
            tablaFacturas.setItems(FXCollections.observableArrayList(facturas));
        }
    }
    @FXML
    private void abrirDialogoPago() {
        Factura facturaSeleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada == null || !facturaSeleccionada.getEstado().equals("Pendiente")) {
            mostrarMensaje("Debe seleccionar una factura 'Pendiente' para pagar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("com/veterinaria/ui/facturacion/PagoSimuladoForm.fxml"));
            loader.setControllerFactory(contextoSpring::getBean);
            Parent root = loader.load();

            PagoSimuladoControlador pagoControlador = loader.getController();
            pagoControlador.initData(facturaSeleccionada);

            Stage stage = new Stage();
            stage.setTitle("Simular Pago para Factura #" + facturaSeleccionada.getIdFactura());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(pagarButton.getScene().getWindow());

            stage.setOnHidden(e -> cargarFacturas());

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir el simulador de pago: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void guardarPdfFactura() {
        Factura facturaSeleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada == null) {
            mostrarMensaje("Seleccione una factura de la tabla para descargar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Factura en PDF");
        fileChooser.setInitialFileName("Factura-" + facturaSeleccionada.getIdFactura() + ".pdf");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(pdfButton.getScene().getWindow());

        if (file != null) {
            try {
                reporteServicio.crearPdfFactura(facturaSeleccionada, file);

                mostrarMensaje("PDF de la factura #" + facturaSeleccionada.getIdFactura() + " guardado con éxito.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                mostrarMensaje("Error al generar el PDF: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
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
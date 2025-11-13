package com.veterinaria.ui.facturacion;

import com.veterinaria.negocio.servicio.IFacturaServicio;
import com.veterinaria.sistema.entidad.Factura;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PagoSimuladoControlador {

    @Autowired
    private IFacturaServicio facturaServicio;

    @FXML
    private Label facturaIdLabel;
    @FXML
    private Label montoLabel;
    @FXML
    private MFXButton aprobarButton;
    @FXML
    private MFXButton rechazarButton;

    private Factura facturaAPagar;

    public void initData(Factura factura) {
        this.facturaAPagar = factura;
        facturaIdLabel.setText("Factura #" + factura.getIdFactura());
        montoLabel.setText(String.format("$ %.2f", factura.getTotal()));
    }

    @FXML
    private void aprobarPago() {
        if (this.facturaAPagar == null) {
            mostrarMensaje("Error fatal: No se ha seleccionado ninguna factura.", Alert.AlertType.ERROR);
            return;
        }

        try {
            facturaServicio.registrarPago(
                    facturaAPagar.getIdFactura(),
                    "Pago Simulado",
                    facturaAPagar.getTotal()
            );

            mostrarMensaje("¡Pago Aprobado! La factura #" + facturaAPagar.getIdFactura() + " ha sido marcada como 'Pagada'.", Alert.AlertType.INFORMATION);
            cerrarVentana();

        } catch (RuntimeException e) {
            mostrarMensaje("Error al registrar el pago: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void rechazarPago() {
        mostrarMensaje("Pago Rechazado. La factura sigue 'Pendiente'.", Alert.AlertType.WARNING);
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) aprobarButton.getScene().getWindow();
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
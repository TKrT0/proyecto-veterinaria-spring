package com.veterinaria.ui.mascota;

import com.veterinaria.negocio.servicio.IMascotaServicio;
import com.veterinaria.sistema.entidad.Mascota;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDate;

@Component
public class MascotaControlador {
    @Autowired
    private IMascotaServicio mascotaServicio;
    @FXML
    private TextField nombreTexto;
    @FXML
    private TextField especieTexto;
    @FXML
    private TextField razaTexto;
    @FXML
    private TextField sexoTexto;
    @FXML
    private DatePicker fechaNacimientoPicker;
    @FXML
    private Button guardarMascotaButton;

    private Integer idClienteDueno;

    public void iniciarDatos(Integer idClienteDueno){
        this.idClienteDueno = idClienteDueno;
    }

    @FXML
    public void registrarMascota(){
        String nombre = nombreTexto.getText();
        String especie = especieTexto.getText();
        String raza = razaTexto.getText();
        String sexo = sexoTexto.getText();
        LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();

        if(nombre.isEmpty() || especie.isEmpty() || raza.isEmpty() || sexo.isEmpty() || fechaNacimiento == null){
            mostrarMensaje("ERROR: Todos los campos deben de ser obligatorios", Alert.AlertType.WARNING);
            return;
        }

        if(this.idClienteDueno == null){
            mostrarMensaje("ERROR: El cliente no se pudo identificar. Intentelo de nuevo", Alert.AlertType.ERROR);
            return;
        }

        Mascota nuevaMascota = new Mascota();
        nuevaMascota.setNombre(nombre);
        nuevaMascota.setEspecie(especie);
        nuevaMascota.setRaza(raza);
        nuevaMascota.setSexo(sexo);
        nuevaMascota.setFechaNacimiento(fechaNacimiento);

        try {
            Mascota mascotaGuardada = mascotaServicio.registrarMascota(nuevaMascota, this.idClienteDueno);

            if (mascotaGuardada != null) {
                mostrarMensaje("Mascota guardada con éxito. ID: " + mascotaGuardada.getIdMascota(), Alert.AlertType.INFORMATION);
                cerrarVentana();
            } else {
                mostrarMensaje("Error al registrar la mascota (el cliente no fue encontrado).", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarMensaje("Error al registrar la mascota: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cerrarVentana(){
        Stage stage = (Stage) guardarMascotaButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

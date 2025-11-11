package com.veterinaria.ui.cliente;

import com.veterinaria.ui.cita.CitaControlador;
import com.veterinaria.ui.historial_clinico.HistorialClinicoControlador;
import com.veterinaria.ui.mascota.MascotaControlador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.veterinaria.negocio.servicio.ICitaServicio;
import com.veterinaria.negocio.servicio.IClienteServicio;
import com.veterinaria.negocio.servicio.IHistorialClinicoServicio;
import com.veterinaria.negocio.servicio.IMascotaServicio;
import com.veterinaria.sistema.entidad.Cliente;
import com.veterinaria.sistema.entidad.Mascota;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;

import animatefx.animation.FadeInUp;
import javafx.scene.layout.BorderPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteControlador {

    @Autowired
    private IClienteServicio clienteServicio;
    @Autowired
    private IMascotaServicio mascotaServicio;
    @Autowired
    private ConfigurableApplicationContext contextoSpring;

    @Autowired
    private ICitaServicio citaServicio;
    @Autowired
    private IHistorialClinicoServicio historialServicio;

    @FXML
    private MFXTextField nombreTexto;
    @FXML
    private MFXTextField apellidoTexto;
    @FXML
    private MFXTextField telefonoTexto;
    @FXML
    private MFXTextField emailTexto;
    @FXML
    private MFXTextField direccionTexto;

    @FXML
    private TableView<Cliente> clientesTabla;
    @FXML
    private TableColumn<Cliente, Integer> colId;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colApellido;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colEmail;

    @FXML
    private TableView<Mascota> mascotasTabla;
    @FXML
    private TableColumn<Mascota, Integer> colMascotaId;
    @FXML
    private TableColumn<Mascota, String> colMascotaNombre;
    @FXML
    private TableColumn<Mascota, String> colMascotaEspecie;
    @FXML
    private TableColumn<Mascota, String> colMascotaRaza;

    @FXML
    private MFXButton agregarMascotaButton;
    @FXML
    private MFXButton verHistorialButton;
    @FXML
    private MFXButton historialButton;
    @FXML
    private MFXButton programarCitaButton;

    @FXML
    private BorderPane root;

    public void marcarError(MFXTextField field){
        field.getStyleClass().remove("input-succes");
        if(!field.getStyleClass().contains("input-error")){
            field.getStyleClass().add("input-error");
        }
    }

    private void marcarOk(MFXTextField field){
        field.getStyleClass().remove("input-error");
        if(!field.getStyleClass().contains("input-success")){
            field.getStyleClass().add("input-success");
        }
    }

    private void limpiarEstadosCampos() {
        nombreTexto.getStyleClass().removeAll("input-error", "input-success");
        apellidoTexto.getStyleClass().removeAll("input-error", "input-success");
        telefonoTexto.getStyleClass().removeAll("input-error", "input-success");
        emailTexto.getStyleClass().removeAll("input-error", "input-success");
        direccionTexto.getStyleClass().removeAll("input-error", "input-success");
    }

    @FXML
    public void initialize() {
        new FadeInUp(root).setSpeed(0.7).play();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colMascotaId.setCellValueFactory(new PropertyValueFactory<>("idMascota"));
        colMascotaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMascotaEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colMascotaRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));

        clientesTabla.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, clienteSeleccionado) -> {
                    if (clienteSeleccionado != null) {
                        listarMascotas(clienteSeleccionado.getId());
                    } else {
                        mascotasTabla.setItems(null);
                    }
                }
        );
        listarClientes();
    }

    @FXML
    private void registrarCliente() {
        if (!validarCampos()) {
            return;
        }

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(nombreTexto.getText());
        nuevoCliente.setApellido(apellidoTexto.getText());
        nuevoCliente.setTelefono(telefonoTexto.getText());
        nuevoCliente.setEmail(emailTexto.getText());
        nuevoCliente.setDireccion(direccionTexto.getText());

        try {
            Cliente clienteGuardado = clienteServicio.registrarCliente(nuevoCliente);

            mostrarMensaje("Cliente registrado con éxito, ID: " + clienteGuardado.getId(), Alert.AlertType.INFORMATION);

            limpiarFormulario();
            listarClientes();

        } catch (Exception ex) {
            mostrarMensaje("Error al registrar el cliente: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void listarClientes() {
        List<Cliente> clientes = clienteServicio.listarClientes();
        ObservableList<Cliente> listaClientes = FXCollections.observableArrayList(clientes);
        clientesTabla.setItems(listaClientes);
    }

    private void listarMascotas(Integer idCliente) {
        List<Mascota> mascotas = mascotaServicio.listarMascotasPorCliente(idCliente);
        mascotasTabla.setItems(FXCollections.observableArrayList(mascotas));
    }

    private void limpiarFormulario() {
        nombreTexto.clear();
        apellidoTexto.clear();
        telefonoTexto.clear();
        emailTexto.clear();
        direccionTexto.clear();
    }

    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String TEL_REGEX = "^[0-9]{10}$";

    private boolean validarCampos() {
        limpiarEstadosCampos();

        List<String> errores = new ArrayList<>();
        String nombre = nombreTexto.getText().trim();
        String apellido = apellidoTexto.getText().trim();
        String telefono = telefonoTexto.getText().trim();
        String email = emailTexto.getText().trim();

        if (nombre.isEmpty()){
            errores.add("El nombre es obligatorio");
            marcarError(nombreTexto);
        } else {
            marcarOk(nombreTexto);
        }
        if (apellido.isEmpty()){
            errores.add("El apellido es obligatorio");
            marcarError(apellidoTexto);
        } else {
            marcarOk(apellidoTexto);
        }
        if (telefono.isEmpty()){
            errores.add("El teléfono es obligatorio");
            marcarError(telefonoTexto);
        }
        if (email.isEmpty()){
            errores.add("El email es obligatorio");
            marcarError(emailTexto);
        }
        if (!telefono.isEmpty() && !telefono.matches(TEL_REGEX)){
            errores.add("El teléfono debe de tener 10 digitos");
            marcarError(telefonoTexto);
        }
        if (!email.isEmpty() && !email.matches(EMAIL_REGEX)){
            errores.add("El formato del email es incorrecto");
            marcarError(emailTexto);
        }
        if (errores.isEmpty()) {
            return true;
        } else {
            String mensajeError = String.join("\n", errores);
            mostrarMensaje(mensajeError, Alert.AlertType.WARNING);
            return false;
        }
    }

    @FXML
    private void abrirDialogoMascota() {
        Cliente clienteSeleccionado = clientesTabla.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            mostrarMensaje("Debe seleccionar un cliente para agregar una mascota", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("com/veterinaria/ui/mascota/MascotaForm.fxml"));

            loader.setControllerFactory(contextoSpring::getBean);

            Parent root = loader.load();

            MascotaControlador mascotaControlador = loader.getController();
            mascotaControlador.iniciarDatos(clienteSeleccionado.getId());

            Stage stage = new Stage();
            stage.setTitle("Registrar Nueva Mascota");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(agregarMascotaButton.getScene().getWindow());

            stage.setOnHidden(e -> listarMascotas(clienteSeleccionado.getId()));

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir el formulario de mascota: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void abrirDialogoCita() {
        Mascota mascotaSeleccionada = mascotasTabla.getSelectionModel().getSelectedItem();

        if (mascotaSeleccionada == null) {
            mostrarMensaje("Debe seleccionar una MASCOTA para programar una cita", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("com/veterinaria/ui/cita/CitaForm.fxml"));

            loader.setControllerFactory(contextoSpring::getBean);

            Parent root = loader.load();

            CitaControlador citaControlador = loader.getController();
            citaControlador.iniciarDatos(mascotaSeleccionada.getIdMascota());

            Stage stage = new Stage();
            stage.setTitle("Programar Cita");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(programarCitaButton.getScene().getWindow());

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir el formulario de citas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void abrirDialogoHistorialClinico() {
        Mascota mascotaSeleccionada = mascotasTabla.getSelectionModel().getSelectedItem();

        if (mascotaSeleccionada == null) {
            mostrarMensaje("Debe seleccionar una MASCOTA para ver su historial", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("com/veterinaria/ui/historial_clinico/HistorialClinicoForm.fxml"));

            loader.setControllerFactory(contextoSpring::getBean);

            Parent root = loader.load();

            HistorialClinicoControlador historialControlador = loader.getController();
            historialControlador.initData(mascotaSeleccionada.getIdMascota());

            Stage stage = new Stage();
            stage.setTitle("Historial Clínico de " + mascotaSeleccionada.getNombre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(verHistorialButton.getScene().getWindow());

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir el historial clínico: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void abrirHistorial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("com/veterinaria/ui/historial/HistorialCitasForm.fxml"));

            loader.setControllerFactory(contextoSpring::getBean);

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Historial General de Citas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(historialButton.getScene().getWindow());

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error al abrir el historial de citas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarMensaje(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

package com.veterinaria.ui.mascota;

import com.veterinaria.negocio.servicio.IClienteServicio;
import com.veterinaria.negocio.servicio.IMascotaServicio;
import com.veterinaria.sistema.entidad.Mascota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.time.LocalDate;

public class MascotaForm extends JDialog {
    private JPanel panelPrincipal;
    private JTextField nombreTexto;
    private JTextField especieTexto;
    private JTextField razaTexto;
    private JTextField sexoTexto;
    private JTextField fechaNacimientoTexto;
    private JButton guardarMascotaButton;

    private IMascotaServicio mascotaServicio;
    private IClienteServicio clienteServicio;
    private Integer idClienteDueno;

    public MascotaForm(JFrame owner, IMascotaServicio mascotaServicio, Integer idClienteDueno) {
        super(owner, "Registro de Mascota", true);
        this.mascotaServicio = mascotaServicio;
        this.idClienteDueno = idClienteDueno;
        iniciarForma(owner);
        guardarMascotaButton.addActionListener(e -> registrarMascota());
    }

    private void registrarMascota() {
        String nombre = nombreTexto.getText();
        String especie = especieTexto.getText();
        String raza = razaTexto.getText();
        String sexo = sexoTexto.getText();
        String fechaNacimiento = fechaNacimientoTexto.getText();

        if(nombreTexto.getText().isEmpty() || especieTexto.getText().isEmpty() || razaTexto.getText().isEmpty() || sexoTexto.getText().isEmpty()){
            mostrarMensaje("Campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Mascota nuevaMascota = new Mascota();
        nuevaMascota.setNombre(nombre);
        nuevaMascota.setEspecie(especie);
        nuevaMascota.setRaza(raza);
        nuevaMascota.setSexo(sexo);

        try{
            nuevaMascota.setFechaNacimiento(LocalDate.parse(fechaNacimientoTexto.getText()));
            Mascota mascotaGuardada = mascotaServicio.registrarMascota(nuevaMascota, this.idClienteDueno);
            if(mascotaGuardada != null){
                mostrarMensaje("Mascota guardada con éxito. ID: " + this.idClienteDueno, null, JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            }
            else{
                mostrarMensaje("Error al registrar la mascota", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e){
            mostrarMensaje("Error al registrar la mascota"+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMensaje(String mensaje, String error, int errorMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, error, errorMensaje);
    }

    private void iniciarForma(JFrame owner) {
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(owner);
    }
}

package com.veterinaria;

import com.formdev.flatlaf.FlatDarkLaf;
import com.veterinaria.ui.cliente.ClienteForm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class VeterinariaApplication {

	public static void main(String[] args) {

        FlatDarkLaf.setup();
		ConfigurableApplicationContext context = new SpringApplicationBuilder(VeterinariaApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            ClienteForm ventana = context.getBean(ClienteForm.class);
            ventana.setVisible(true);
        });

	}
}


// Soy el más pndejo de México - JonaGod

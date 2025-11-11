package com.veterinaria;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class AplicacionVeterinaria {
    private static ConfigurableApplicationContext contextoSpring;

    static void main(String[] args) {
        Application.launch(FxApp.class, args);
    }

    public static class FxApp extends Application {
        @Override
        public void init() throws Exception {
            contextoSpring = new SpringApplicationBuilder(AplicacionVeterinaria.class)
                    .headless(false)
                    .run();
        }

        @Override
        public void start(Stage stage) throws Exception {
            try{

                java.net.URL fxmlUrl = getClass().getClassLoader().getResource("com/veterinaria/ui/cliente/ClienteForm.fxml");

                if (fxmlUrl == null) {
                    throw new java.io.FileNotFoundException("No se puede encontrar el FXML. Asegúrate de que la ruta es correcta: com/veterinaria/ui/cliente/ClienteForm.fxml");
                }

                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
                fxmlLoader.setControllerFactory(contextoSpring::getBean);

                Parent rootNode = fxmlLoader.load();

                Scene escena = new Scene(rootNode);

                stage.setScene(escena);
                stage.setTitle("Veterinaria");

                stage.setOnCloseRequest( e -> {
                    Platform.exit();
                    System.exit(0);
                });

                stage.show();

            } catch (IOException e){
                e.printStackTrace();
                System.err.println("Error al cargar la vista, asegurate de que la ruta sea correcta");
                Platform.exit();
            }
        }

        @Override
        public void stop() throws Exception {
            if (contextoSpring != null) {
                contextoSpring.close();
            }
        }
    }
}
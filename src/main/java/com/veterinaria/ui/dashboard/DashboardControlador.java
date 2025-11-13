package com.veterinaria.ui.dashboard;

import com.veterinaria.negocio.dto.ConteoPorEspecieDTO;
import com.veterinaria.negocio.dto.VentasPorServicioDTO;
import com.veterinaria.negocio.servicio.IDashboardServicio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardControlador {

    @Autowired
    private IDashboardServicio dashboardServicio;

    @FXML
    private Label ingresosLabel;
    @FXML
    private Label clientesLabel;
    @FXML
    private PieChart graficoEspecies;
    @FXML
    private BarChart<String, Number> graficoVentas;

    @FXML
    public void initialize() {
        cargarEstadisticas();
        cargarGraficoEspecies();
        cargarGraficoVentas();
    }

    private void cargarEstadisticas() {
        Double totalVentas = dashboardServicio.obtenerTotalVentas();
        Long totalClientes = dashboardServicio.obtenerTotalClientes();

        ingresosLabel.setText(String.format("$ %.2f", totalVentas));
        clientesLabel.setText(String.valueOf(totalClientes));
    }

    private void cargarGraficoEspecies() {
        List<ConteoPorEspecieDTO> conteos = dashboardServicio.obtenerConteoPorEspecie();

        ObservableList<PieChart.Data> datosGrafico = FXCollections.observableArrayList();
        for (ConteoPorEspecieDTO conteo : conteos) {
            datosGrafico.add(new PieChart.Data(
                    conteo.especie() + " (" + conteo.conteo() + ")", // Etiqueta
                    conteo.conteo() // Valor
            ));
        }

        graficoEspecies.setData(datosGrafico);
    }

    private void cargarGraficoVentas() {
        List<VentasPorServicioDTO> topServicios = dashboardServicio.obtenerTopServicios();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos por Servicio");

        for (VentasPorServicioDTO servicio : topServicios) {
            series.getData().add(new XYChart.Data<>(
                    servicio.nombreServicio(), // Eje X (String)
                    servicio.totalVendido()    // Eje Y (Number)
            ));
        }

        graficoVentas.getData().add(series);
    }
}
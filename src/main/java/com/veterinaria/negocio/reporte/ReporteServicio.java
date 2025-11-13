package com.veterinaria.negocio.reporte;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.veterinaria.sistema.entidad.Factura;
import com.veterinaria.sistema.entidad.LineaFactura;
import com.veterinaria.sistema.repositorio.io.FacturaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class ReporteServicio {

    @Autowired
    private FacturaRepositorio facturaRepositorio;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void crearPdfFactura(Factura factura, File destino) throws IOException {

        Factura facturaCompleta = facturaRepositorio.findByIdWithLineas(factura.getIdFactura())
                .orElseThrow(() -> new RuntimeException("Error: No se pudo encontrar la factura para generar el PDF."));

        PdfWriter writer = new PdfWriter(destino);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            Paragraph titulo = new Paragraph("Factura de Veterinaria")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20)
                    .setMarginBottom(20);
            document.add(titulo);

            document.add(new Paragraph("Factura #: " + facturaCompleta.getIdFactura()));
            document.add(new Paragraph("Fecha de Emisión: " + facturaCompleta.getFechaEmision().format(formatter)));
            document.add(new Paragraph("Cliente: " + facturaCompleta.getCliente().getNombre() + " " + facturaCompleta.getCliente().getApellido()));
            document.add(new Paragraph("Email: " + facturaCompleta.getCliente().getEmail()));
            document.add(new Paragraph("Estado: " + facturaCompleta.getEstado().toUpperCase())
                    .setBold()
                    .setMarginBottom(20));

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            tabla.addHeaderCell(new Cell().add(new Paragraph("Servicio").setBold()));
            tabla.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold()));
            tabla.addHeaderCell(new Cell().add(new Paragraph("P. Unit.").setBold()));
            tabla.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setBold()));

            for (LineaFactura linea : facturaCompleta.getLineas()) {
                tabla.addCell(linea.getServicio().getNombre());
                tabla.addCell(String.valueOf(linea.getCantidad()));
                tabla.addCell(String.format("$ %.2f", linea.getPrecioUnitario()));
                tabla.addCell(String.format("$ %.2f", linea.getSubtotal()));
            }

            document.add(tabla);

            Paragraph total = new Paragraph("TOTAL: " + String.format("$ %.2f", facturaCompleta.getTotal()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBold()
                    .setFontSize(18)
                    .setMarginTop(20);
            document.add(total);

        } finally {
            document.close();
        }
    }
}
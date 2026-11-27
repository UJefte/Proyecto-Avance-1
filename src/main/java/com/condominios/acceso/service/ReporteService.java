package com.condominios.acceso.service;

import com.condominios.acceso.dto.AccesoResponse;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/** Genera el reporte de la bitacora de accesos en PDF y en Excel. */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] COLUMNAS = {"Fecha y hora", "Titular", "Tipo", "Movimiento", "Resultado", "Motivo"};

    // colores de Jefte Grand
    private static final Color OBSIDIANA = new Color(0x15, 0x17, 0x15);
    private static final Color CHAMPAN = new Color(0xC4, 0xAD, 0x83);
    private static final Color AUTORIZADO = new Color(0x3D, 0x70, 0x58);
    private static final Color DENEGADO = new Color(0xA3, 0x4B, 0x4B);

    private final AccesoService accesoService;

    // ---------- PDF (con OpenPDF) ----------

    public byte[] bitacoraPdf() {
        List<AccesoResponse> registros = accesoService.bitacora();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        // hoja horizontal para que quepan las columnas
        Document documento = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(documento, salida);
        documento.open();

        documento.add(new Paragraph("Jefté Grand", FontFactory.getFont(FontFactory.TIMES_BOLD, 22, CHAMPAN)));
        documento.add(new Paragraph("Bitácora de accesos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, OBSIDIANA)));
        documento.add(new Paragraph("Generado el " + LocalDateTime.now().format(FECHA) + "  |  " + registros.size() + " registros",
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY)));
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(COLUMNAS.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{2.2f, 3f, 1.6f, 1.6f, 1.8f, 4f});
        tabla.setHeaderRows(1);

        // encabezados
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String columna : COLUMNAS) {
            PdfPCell celda = new PdfPCell(new Phrase(columna, fuenteEncabezado));
            celda.setBackgroundColor(OBSIDIANA);
            celda.setPadding(6);
            tabla.addCell(celda);
        }

        // un renglon por cada escaneo
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA, 9, OBSIDIANA);
        for (AccesoResponse r : registros) {
            tabla.addCell(celda(r.fechaHora().format(FECHA), fuente));
            tabla.addCell(celda(texto(r.titular()), fuente));
            tabla.addCell(celda(texto(r.tipoPase()), fuente));
            tabla.addCell(celda(texto(r.movimiento()), fuente));
            Color color = "PERMITIDO".equals(r.resultado().name()) ? AUTORIZADO : DENEGADO;
            tabla.addCell(celda(r.resultado().name(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, color)));
            tabla.addCell(celda(texto(r.motivo()), fuente));
        }

        documento.add(tabla);
        documento.close();
        return salida.toByteArray();
    }

    private PdfPCell celda(String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setPadding(5);
        celda.setBorderColor(new Color(0xD9, 0xD2, 0xC5));
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return celda;
    }

    // ---------- Excel (con Apache POI) ----------

    public byte[] bitacoraExcel() {
        List<AccesoResponse> registros = accesoService.bitacora();
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Bitacora");

            // estilo del encabezado: negritas y fondo gris
            CellStyle estiloEncabezado = libro.createCellStyle();
            org.apache.poi.ss.usermodel.Font negritas = libro.createFont();
            negritas.setBold(true);
            estiloEncabezado.setFont(negritas);
            estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row encabezado = hoja.createRow(0);
            for (int i = 0; i < COLUMNAS.length; i++) {
                encabezado.createCell(i).setCellValue(COLUMNAS[i]);
                encabezado.getCell(i).setCellStyle(estiloEncabezado);
            }

            int fila = 1;
            for (AccesoResponse r : registros) {
                Row renglon = hoja.createRow(fila++);
                renglon.createCell(0).setCellValue(r.fechaHora().format(FECHA));
                renglon.createCell(1).setCellValue(texto(r.titular()));
                renglon.createCell(2).setCellValue(texto(r.tipoPase()));
                renglon.createCell(3).setCellValue(texto(r.movimiento()));
                renglon.createCell(4).setCellValue(r.resultado().name());
                renglon.createCell(5).setCellValue(texto(r.motivo()));
            }

            // ancho de las columnas (en unidades de 1/256 de caracter)
            int[] anchos = {18, 26, 14, 12, 13, 40};
            for (int i = 0; i < anchos.length; i++) {
                hoja.setColumnWidth(i, anchos[i] * 256);
            }
            hoja.createFreezePane(0, 1);

            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el Excel", e);
        }
    }

    private String texto(Object valor) {
        return valor == null ? "-" : valor.toString();
    }
}

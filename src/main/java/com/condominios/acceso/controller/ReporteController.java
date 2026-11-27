package com.condominios.acceso.controller;

import com.condominios.acceso.service.ReporteService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private static final MediaType EXCEL =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReporteService reporteService;

    /** Bitacora de accesos en PDF. */
    @GetMapping("/bitacora.pdf")
    public ResponseEntity<byte[]> bitacoraPdf() {
        return descarga(reporteService.bitacoraPdf(), MediaType.APPLICATION_PDF, "pdf");
    }

    /** Bitacora de accesos en Excel. */
    @GetMapping("/bitacora.xlsx")
    public ResponseEntity<byte[]> bitacoraExcel() {
        return descarga(reporteService.bitacoraExcel(), EXCEL, "xlsx");
    }

    // hace que el navegador lo descargue con un nombre como bitacora-2026-11-26.pdf
    private ResponseEntity<byte[]> descarga(byte[] archivo, MediaType tipo, String extension) {
        String nombre = "bitacora-" + LocalDate.now() + "." + extension;
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .body(archivo);
    }
}

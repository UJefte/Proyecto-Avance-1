package com.condominios.acceso.controller;

import com.condominios.acceso.dto.AccesoResponse;
import com.condominios.acceso.dto.SolicitudResponse;
import com.condominios.acceso.dto.ValidarQrRequest;
import com.condominios.acceso.dto.VisitaSinQrRequest;
import com.condominios.acceso.service.AccesoService;
import com.condominios.acceso.service.SolicitudService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accesos")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;
    private final SolicitudService solicitudService;

    /** El guardia escanea un QR: registra entrada o salida y responde PERMITIDO o DENEGADO. */
    @PostMapping("/validar")
    public AccesoResponse validar(@Valid @RequestBody ValidarQrRequest req) {
        return accesoService.validar(req.codigo());
    }

    /** Bitácora de accesos, la más reciente primero. */
    @GetMapping
    public List<AccesoResponse> bitacora() {
        return accesoService.bitacora();
    }

    /** El guardia registra a una visita sin QR; queda pendiente de aprobación del residente. */
    @PostMapping("/visita-sin-qr")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse visitaSinQr(@Valid @RequestBody VisitaSinQrRequest req) {
        return solicitudService.crear(req);
    }
}

package com.condominios.acceso.controller;

import com.condominios.acceso.dto.ResolverSolicitudRequest;
import com.condominios.acceso.dto.SolicitudResponse;
import com.condominios.acceso.entity.EstadoSolicitud;
import com.condominios.acceso.service.SolicitudService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService service;

    @GetMapping
    public List<SolicitudResponse> listar(@RequestParam(required = false) EstadoSolicitud estado) {
        return service.listar(estado);
    }

    /** El residente aprueba o rechaza la visita que espera en caseta. */
    @PutMapping("/{id}/resolver")
    public SolicitudResponse resolver(@PathVariable Long id, @Valid @RequestBody ResolverSolicitudRequest req) {
        return service.resolver(id, req.aprobada());
    }
}

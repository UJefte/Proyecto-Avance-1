package com.condominios.acceso.controller;

import com.condominios.acceso.dto.PaseResponse;
import com.condominios.acceso.dto.PaseVisitaRequest;
import com.condominios.acceso.service.PaseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pases")
@RequiredArgsConstructor
public class PaseController {

    private final PaseService service;

    @PostMapping("/residente/{residenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PaseResponse paraResidente(@PathVariable Long residenteId) {
        return service.crearParaResidente(residenteId);
    }

    @PostMapping("/trabajador/{trabajadorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PaseResponse paraTrabajador(@PathVariable Long trabajadorId) {
        return service.crearParaTrabajador(trabajadorId);
    }

    @PostMapping("/visita")
    @ResponseStatus(HttpStatus.CREATED)
    public PaseResponse paraVisita(@Valid @RequestBody PaseVisitaRequest req) {
        return service.crearParaVisita(req);
    }

    @GetMapping
    public List<PaseResponse> listar() {
        return service.listar();
    }

    @PutMapping("/{id}/desactivar")
    public PaseResponse desactivar(@PathVariable Long id) {
        return service.desactivar(id);
    }
}

package com.condominios.acceso.controller;

import com.condominios.acceso.dto.ResidenteRequest;
import com.condominios.acceso.dto.ResidenteResponse;
import com.condominios.acceso.service.ResidenteService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/residentes")
@RequiredArgsConstructor
public class ResidenteController {

    private final ResidenteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResidenteResponse crear(@Valid @RequestBody ResidenteRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public List<ResidenteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResidenteResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
}

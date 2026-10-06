package com.condominios.acceso.controller;

import com.condominios.acceso.dto.DepartamentoRequest;
import com.condominios.acceso.dto.DepartamentoResponse;
import com.condominios.acceso.service.DepartamentoService;
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
@RequestMapping("/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartamentoResponse crear(@Valid @RequestBody DepartamentoRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public List<DepartamentoResponse> listar() {
        return service.listar();
    }
}

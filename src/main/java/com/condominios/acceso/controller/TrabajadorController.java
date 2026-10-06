package com.condominios.acceso.controller;

import com.condominios.acceso.dto.TrabajadorRequest;
import com.condominios.acceso.dto.TrabajadorResponse;
import com.condominios.acceso.service.TrabajadorService;
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
@RequestMapping("/trabajadores")
@RequiredArgsConstructor
public class TrabajadorController {

    private final TrabajadorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrabajadorResponse crear(@Valid @RequestBody TrabajadorRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public List<TrabajadorResponse> listar() {
        return service.listar();
    }
}

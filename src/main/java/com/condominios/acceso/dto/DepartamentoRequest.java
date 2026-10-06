package com.condominios.acceso.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartamentoRequest(
        @NotBlank String numero,
        String torre) {
}

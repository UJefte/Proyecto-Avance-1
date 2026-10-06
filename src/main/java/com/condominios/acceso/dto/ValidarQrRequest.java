package com.condominios.acceso.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidarQrRequest(@NotBlank String codigo) {
}

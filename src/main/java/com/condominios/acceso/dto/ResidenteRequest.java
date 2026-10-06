package com.condominios.acceso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResidenteRequest(
        @NotBlank String nombre,
        @NotBlank @Email String correo,
        @NotNull Long departamentoId) {
}

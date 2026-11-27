package com.condominios.acceso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResidenteRequest(
        @NotBlank String nombre,
        @NotBlank @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[a-zA-Z]{2,}$", message = "debe ser un correo valido, por ejemplo nombre@dominio.com") String correo,
        @NotNull Long departamentoId) {
}

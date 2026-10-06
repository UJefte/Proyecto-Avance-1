package com.condominios.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record TrabajadorRequest(
        @NotBlank String nombre,
        @NotBlank String puesto,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin) {
}

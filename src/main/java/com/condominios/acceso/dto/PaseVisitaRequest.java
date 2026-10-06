package com.condominios.acceso.dto;

import com.condominios.acceso.entity.TipoVisita;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Pase temporal que genera un residente para su invitado. */
public record PaseVisitaRequest(
        @NotBlank String nombreVisitante,
        @Email String correoVisitante,
        @NotNull TipoVisita tipo,
        @NotNull Long residenteId,
        @NotNull LocalDateTime vigenteDesde,
        @NotNull LocalDateTime vigenteHasta,
        /** 1 = pase único. Null = sin límite de entradas (visita recurrente). */
        @Min(1) Integer usosMaximos,
        /** Opcional, ej. "MONDAY,FRIDAY". */
        String diasPermitidos) {
}

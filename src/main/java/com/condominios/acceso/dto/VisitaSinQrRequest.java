package com.condominios.acceso.dto;

import com.condominios.acceso.entity.TipoVisita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Lo captura el guardia cuando llega alguien sin pase. */
public record VisitaSinQrRequest(
        @NotBlank String nombreVisitante,
        @NotNull TipoVisita tipo,
        @NotNull Long residenteId,
        String motivo,
        String fotoUrl) {
}

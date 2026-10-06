package com.condominios.acceso.dto;

import jakarta.validation.constraints.NotNull;

public record ResolverSolicitudRequest(@NotNull Boolean aprobada) {
}

package com.condominios.acceso.dto;

import com.condominios.acceso.entity.RegistroAcceso;
import com.condominios.acceso.entity.ResultadoAcceso;
import com.condominios.acceso.entity.TipoMovimiento;
import com.condominios.acceso.entity.TipoPase;
import java.time.LocalDateTime;

public record AccesoResponse(
        Long id,
        String codigo,
        TipoPase tipoPase,
        String titular,
        TipoMovimiento movimiento,
        ResultadoAcceso resultado,
        String motivo,
        LocalDateTime fechaHora) {

    public static AccesoResponse from(RegistroAcceso r) {
        return new AccesoResponse(
                r.getId(),
                r.getCodigoLeido(),
                r.getPase() != null ? r.getPase().getTipo() : null,
                r.getPase() != null ? r.getPase().nombreTitular() : null,
                r.getMovimiento(),
                r.getResultado(),
                r.getMotivo(),
                r.getFechaHora());
    }
}

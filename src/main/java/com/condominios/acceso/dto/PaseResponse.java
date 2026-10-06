package com.condominios.acceso.dto;

import com.condominios.acceso.entity.PaseQR;
import com.condominios.acceso.entity.TipoPase;
import java.time.LocalDateTime;

public record PaseResponse(
        Long id,
        String codigo,
        TipoPase tipo,
        String titular,
        LocalDateTime vigenteDesde,
        LocalDateTime vigenteHasta,
        Integer usosMaximos,
        int usosActuales,
        String diasPermitidos,
        boolean activo) {

    public static PaseResponse from(PaseQR p) {
        return new PaseResponse(p.getId(), p.getCodigo(), p.getTipo(), p.nombreTitular(),
                p.getVigenteDesde(), p.getVigenteHasta(), p.getUsosMaximos(), p.getUsosActuales(),
                p.getDiasPermitidos(), p.isActivo());
    }
}

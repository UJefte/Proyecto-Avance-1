package com.condominios.acceso.dto;

import com.condominios.acceso.entity.EstadoSolicitud;
import com.condominios.acceso.entity.SolicitudVisita;
import com.condominios.acceso.entity.TipoVisita;
import java.time.LocalDateTime;

public record SolicitudResponse(
        Long id,
        String visitante,
        TipoVisita tipo,
        Long residenteId,
        String residente,
        String motivo,
        EstadoSolicitud estado,
        LocalDateTime creadaEn,
        LocalDateTime resueltaEn,
        String codigoPase) {

    public static SolicitudResponse from(SolicitudVisita s) {
        return new SolicitudResponse(
                s.getId(),
                s.getVisita().getNombre(),
                s.getVisita().getTipo(),
                s.getResidente().getId(),
                s.getResidente().getNombre(),
                s.getMotivo(),
                s.getEstado(),
                s.getCreadaEn(),
                s.getResueltaEn(),
                s.getPase() != null ? s.getPase().getCodigo() : null);
    }
}

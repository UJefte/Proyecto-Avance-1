package com.condominios.acceso.dto;

import com.condominios.acceso.entity.Trabajador;
import java.time.LocalTime;

public record TrabajadorResponse(Long id, String nombre, String puesto, LocalTime horaInicio, LocalTime horaFin) {

    public static TrabajadorResponse from(Trabajador t) {
        return new TrabajadorResponse(t.getId(), t.getNombre(), t.getPuesto(), t.getHoraInicio(), t.getHoraFin());
    }
}

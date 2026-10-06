package com.condominios.acceso.dto;

import com.condominios.acceso.entity.Residente;

public record ResidenteResponse(Long id, String nombre, String correo, Long departamentoId, String departamento) {

    public static ResidenteResponse from(Residente r) {
        String etiqueta = r.getDepartamento().getTorre() == null
                ? r.getDepartamento().getNumero()
                : r.getDepartamento().getTorre() + "-" + r.getDepartamento().getNumero();
        return new ResidenteResponse(r.getId(), r.getNombre(), r.getCorreo(), r.getDepartamento().getId(), etiqueta);
    }
}

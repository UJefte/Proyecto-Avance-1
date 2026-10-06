package com.condominios.acceso.dto;

import com.condominios.acceso.entity.Departamento;

public record DepartamentoResponse(Long id, String numero, String torre) {

    public static DepartamentoResponse from(Departamento d) {
        return new DepartamentoResponse(d.getId(), d.getNumero(), d.getTorre());
    }
}

package com.condominios.acceso.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String entidad, Long id) {
        super(entidad + " con id " + id + " no existe");
    }
}

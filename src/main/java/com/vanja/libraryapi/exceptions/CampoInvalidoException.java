package com.vanja.libraryapi.exceptions;

import lombok.Getter;

public class CampoInvalidoException extends RuntimeException{

    @Getter
    private String campo;

    public CampoInvalidoException(String Campo, String mensagem){
        super(mensagem);
        this.campo = campo;
    }

}

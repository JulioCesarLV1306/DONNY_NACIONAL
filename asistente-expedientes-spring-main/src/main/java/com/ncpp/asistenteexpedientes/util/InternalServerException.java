package com.ncpp.asistenteexpedientes.util;

import org.springframework.web.bind.annotation.ResponseStatus;  
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 
public class InternalServerException extends RuntimeException{
    public InternalServerException(){
        super();
    }
}

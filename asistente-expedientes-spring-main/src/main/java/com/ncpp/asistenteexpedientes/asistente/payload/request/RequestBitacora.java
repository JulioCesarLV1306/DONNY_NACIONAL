package com.ncpp.asistenteexpedientes.asistente.payload.request;

import com.ncpp.asistenteexpedientes.asistente.entity.Modulo;
import com.ncpp.asistenteexpedientes.sece.entity.Persona;

import lombok.Data;
@Data
public class RequestBitacora {
    private Modulo modulo;
    private Persona persona;
    private String codigo;
    private String descripcion;

}

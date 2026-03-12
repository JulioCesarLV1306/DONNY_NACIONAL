package com.ncpp.asistenteexpedientes.sij.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestBusquedaExpediente {
    private int numero;
    private int anio;
    private int cuaderno;
    private String especialidad;
    private String dni;
    private int idTipo;
}

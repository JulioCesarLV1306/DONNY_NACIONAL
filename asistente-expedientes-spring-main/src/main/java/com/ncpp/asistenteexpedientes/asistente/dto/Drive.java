package com.ncpp.asistenteexpedientes.asistente.dto;

import lombok.Data;

@Data
public class Drive {

    private String letraUnidad;
    private String nombre;
    private String tipo;
    private String espacioTotalHR;
    private String espacioLibreHR;
    private long espacioTotal;
    private long espacioLibre;
    
}

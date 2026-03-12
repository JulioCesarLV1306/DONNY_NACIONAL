package com.ncpp.asistenteexpedientes.sij.entity;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;


@Data 
public class Expediente implements Serializable{
    private static final long serialVersionUID = 746237126088051312L;
    private String nUnico;
    private int nIncidente;
    private String formatoExpediente;
    private String sumilla;
    private String instanciaActual;
    private String ubicacionActual;
    private String especialistaLegal;
    private Date fechaIngreso;
}

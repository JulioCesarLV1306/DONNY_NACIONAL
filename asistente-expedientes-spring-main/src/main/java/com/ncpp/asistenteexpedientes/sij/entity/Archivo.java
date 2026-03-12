package com.ncpp.asistenteexpedientes.sij.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Archivo implements Serializable{
    private static final long serialVersionUID = 746237126088051312L;
    private String nombre;
    private String fecha;
    private String ruta;
    private ServidorFtp servidorFtp;
}

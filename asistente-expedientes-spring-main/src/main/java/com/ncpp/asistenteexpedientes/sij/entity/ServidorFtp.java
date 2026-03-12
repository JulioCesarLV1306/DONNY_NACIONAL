package com.ncpp.asistenteexpedientes.sij.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ServidorFtp implements Serializable{
    private static final long serialVersionUID = 746237126088051312L;
    private String nombreUsuario;
    private String claveUsuario;
    private String ipServer;
}

package com.ncpp.asistenteexpedientes.sij.entity;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;

@Data
public class Fecha implements Serializable{
    private static final long serialVersionUID = 746237126088051312L;
    private Date fecha;
    private String sumilla;
}

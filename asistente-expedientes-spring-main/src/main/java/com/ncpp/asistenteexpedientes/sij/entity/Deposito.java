package com.ncpp.asistenteexpedientes.sij.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Deposito {
    
    private String numDeposito;
    private String depositante;
    private String moneda;
    private float monto;
    private String estado;
    
    
}

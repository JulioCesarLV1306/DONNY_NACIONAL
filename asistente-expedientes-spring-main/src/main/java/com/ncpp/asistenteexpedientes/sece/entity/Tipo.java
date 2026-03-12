package com.ncpp.asistenteexpedientes.sece.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="Tipo")
@Data
public class Tipo implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;
    
    @Id
    @Column(name="id_tipo")
    private int idTipo;

    @Column(name = "nombre")
    private String nombre;
}

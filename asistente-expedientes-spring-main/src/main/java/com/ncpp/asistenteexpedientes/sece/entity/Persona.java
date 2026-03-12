package com.ncpp.asistenteexpedientes.sece.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="Persona")
@Data
public class Persona implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;

    @Id
    @Column(name="id_persona")
    private int idPersona;

    @Column(name = "dni")
    private String dni;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "correo")
    private String correo;

    @Column(name = "estado")
    private int estado;

    @Column(name = "numero")
    private String numero;

    @ManyToOne
    @JoinColumn(name = "id_tipo")
    private Tipo tipo;

}


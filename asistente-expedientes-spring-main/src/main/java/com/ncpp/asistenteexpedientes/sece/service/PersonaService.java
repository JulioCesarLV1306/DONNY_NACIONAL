package com.ncpp.asistenteexpedientes.sece.service;

import com.ncpp.asistenteexpedientes.sece.entity.Persona;

public interface PersonaService {
    public Persona buscarPorDni(String dni);
}

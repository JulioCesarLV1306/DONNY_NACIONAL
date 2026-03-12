package com.ncpp.asistenteexpedientes.asistente.service;

import com.ncpp.asistenteexpedientes.asistente.entity.Encuesta;

public interface EncuestaService {
    public void create(Encuesta encuesta);
    public Encuesta findByDni(String dni);
}

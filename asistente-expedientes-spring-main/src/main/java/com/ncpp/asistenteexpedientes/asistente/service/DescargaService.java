package com.ncpp.asistenteexpedientes.asistente.service;

import com.ncpp.asistenteexpedientes.asistente.entity.Descarga;

public interface DescargaService {
   // public Descarga create(String keyDescarga);
    public Descarga createOrUpdateDescarga(String keyDescarga, String estado, int archivosDescargados, int totalArchivos);
    public Descarga addMensajeFinal(String keyDescarga, String mensajeFinal);
    public Descarga find(String keyDescarga);
}

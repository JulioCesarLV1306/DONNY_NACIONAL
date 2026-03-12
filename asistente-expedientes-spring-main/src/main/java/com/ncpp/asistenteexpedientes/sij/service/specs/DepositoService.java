package com.ncpp.asistenteexpedientes.sij.service.specs;

public interface DepositoService {
    public boolean generarReporte(String keyDescarga, String rutaDescarga, String nUnico, int nIncidente, String formatoExpediente);
    public int getConteo( String nUnico, int nIncidente);
}

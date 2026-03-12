package com.ncpp.asistenteexpedientes.asistente.service;

public interface EstadisticasService {
    public void aumentarActas(long idModulo, int cantidad);
    public void aumentarResoluciones(long idModulo, int cantidad);
    public void aumentarDocumentos(long idModulo, int cantidad);
    public void aumentarVideos(long idModulo, int cantidad);
    public void aumentarHojas(long idModulo, int cantidad);
    public void aumentarBytes(long idModulo, long cantidad);
    public void aumentarPenal(long idModulo, int cantidad);
    public void aumentarLaboral(long idModulo, int cantidad);
    public void aumentarCivil(long idModulo, int cantidad);
    public void aumentarFamilia(long idModulo, int cantidad);
}

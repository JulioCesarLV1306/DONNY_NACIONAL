package com.ncpp.asistenteexpedientes.asistente.service;

import java.sql.Date;
import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.entity.Estadisticas;

public interface EstadisticasService {
    public Estadisticas obtenerEstadisticasHoy(long idModulo);
    public List<Estadisticas> listarEstadisticasHoy();
    public List<Estadisticas> listarEstadisticasPorRango(Date fechaInicio, Date fechaFin);
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

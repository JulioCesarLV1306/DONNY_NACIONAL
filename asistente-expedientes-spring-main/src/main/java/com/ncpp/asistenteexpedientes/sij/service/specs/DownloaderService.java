package com.ncpp.asistenteexpedientes.sij.service.specs;

import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.dto.Drive;
import com.ncpp.asistenteexpedientes.asistente.entity.Descarga;
import com.ncpp.asistenteexpedientes.sij.payload.response.ResponseTamanioDescarga;

public interface DownloaderService {
    public List<ResponseTamanioDescarga> getTamanioDescarga(String nUnico, int nIncidente, String[] fechas, String[] keysEleccion);
    public Descarga descargar(String expediente,String nUnico, int nIncidente, String[] fechas, String keyEleccion, long tamanio, Drive drive, String ipModulo,long idModulo,String dniPersona);
    //public Descarga copiar(String nUnico, int nIncidente, String[] fechas, String keyEleccion, Drive drive, Modulo modulo,String dniPersona);
    public Descarga consultar(String ipModulo,String dniPersona,String nUnico, int nIncidente, String keyEleccion,String[] fechas);
    //public void olvidar(String ipModulo,String dniPersona,String nUnico, int nIncidente, String keyEleccion,String[] fechas);
}

package com.ncpp.asistenteexpedientes.sij.service.specs;

import java.util.List;

import com.ncpp.asistenteexpedientes.sij.entity.Expediente;
import com.ncpp.asistenteexpedientes.sij.payload.request.RequestBusquedaExpediente;
import com.ncpp.asistenteexpedientes.sij.payload.response.ResponseEleccionModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface ExpedienteService {
    public Page<Expediente> buscarPorDNI(String dni, String especialidad, Pageable page);
    public Page<Expediente> buscar(RequestBusquedaExpediente request,Pageable page);
    public List<ResponseEleccionModel> getConteos( String nUnico, int nIncidente);
}
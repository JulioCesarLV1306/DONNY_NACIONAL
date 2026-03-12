package com.ncpp.asistenteexpedientes.sij.service.specs;

import java.util.List;

import com.ncpp.asistenteexpedientes.sij.entity.Archivo;
import com.ncpp.asistenteexpedientes.sij.entity.Fecha;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VideoService {
    public Page<Fecha> getFechas(String nUnico, int nIncidente,Pageable page);
    public List<Archivo> buscarTodo(String nUnico, int nIncidente);
    public List<Archivo> buscarPorFechas(String nUnico, int nIncidente, String[] fechas);
}

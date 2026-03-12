package com.ncpp.asistenteexpedientes.sij.payload.request;

import com.ncpp.asistenteexpedientes.asistente.dto.Drive;
import com.ncpp.asistenteexpedientes.asistente.entity.Modulo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestCopiar {
    private Drive drive;
    private Modulo modulo;
}

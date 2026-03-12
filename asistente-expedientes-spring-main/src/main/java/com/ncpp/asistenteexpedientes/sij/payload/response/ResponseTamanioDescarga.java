package com.ncpp.asistenteexpedientes.sij.payload.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseTamanioDescarga {
    private String key;
    private long tamanio;
    private String tamanioHR;
}

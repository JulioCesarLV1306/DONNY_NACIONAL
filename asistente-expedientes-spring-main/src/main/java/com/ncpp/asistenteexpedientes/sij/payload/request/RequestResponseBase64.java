package com.ncpp.asistenteexpedientes.sij.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestResponseBase64 {
    private String extension;
    private String data;
}

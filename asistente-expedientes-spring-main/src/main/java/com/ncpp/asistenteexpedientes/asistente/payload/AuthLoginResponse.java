package com.ncpp.asistenteexpedientes.asistente.payload;

import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Respuesta de autenticación")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResponse {

    @Schema(description = "Indica si la autenticación fue exitosa", example = "true")
    private boolean autenticado;

    @Schema(description = "Mensaje descriptivo del resultado", example = "Autenticación exitosa")
    private String mensaje;

    @Schema(description = "Datos del usuario autenticado. Null cuando autenticado=false")
    private Usuario usuario;
}

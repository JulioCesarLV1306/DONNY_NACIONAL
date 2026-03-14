package com.ncpp.asistenteexpedientes.asistente.payload.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Credenciales de acceso de usuario")
@Data
public class AuthLoginRequest {

    @Schema(description = "Correo registrado del usuario", example = "usuario@example.com", required = true)
    @JsonProperty("x_correo")
    @JsonAlias({"xCorreo", "xcorreo", "correo"})
    private String xCorreo;

    @Schema(description = "Clave de acceso (DNI del usuario)", example = "12345678", required = true)
    @JsonProperty("c_dni")
    @JsonAlias({"cDni", "cdni", "dni", "clave"})
    private String cDni;
}

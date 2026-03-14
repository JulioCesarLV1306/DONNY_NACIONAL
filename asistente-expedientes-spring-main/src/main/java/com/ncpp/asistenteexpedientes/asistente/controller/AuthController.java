package com.ncpp.asistenteexpedientes.asistente.controller;

import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.payload.AuthLoginResponse;
import com.ncpp.asistenteexpedientes.asistente.payload.request.AuthLoginRequest;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
import com.ncpp.asistenteexpedientes.util.Constants;
import com.ncpp.asistenteexpedientes.util.InternalServerException;
import com.ncpp.asistenteexpedientes.util.LogDony;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticación", description = "API para autenticación de usuarios internos con correo y DNI")
@CrossOrigin(origins = "*", methods = { RequestMethod.POST })
@RestController
@RequestMapping(Constants.VERSION_API + "/auth")
public class AuthController {

    private UsuarioServiceImpl usuarioService;

    public AuthController() {
        usuarioService = new UsuarioServiceImpl();
    }

    @Operation(
        summary = "Login con correo y DNI",
        description = "Autentica al usuario usando x_correo (correo) y c_dni (clave). " +
            "Solo permite usuarios activos (l_activo = 'S')."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticación exitosa",
            content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas o usuario inactivo",
            content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthLoginResponse> login(
        @Parameter(description = "Credenciales de acceso", required = true, schema = @Schema(implementation = AuthLoginRequest.class))
        @RequestBody AuthLoginRequest request) {
        try {
            if (request == null || request.getXCorreo() == null || request.getCDni() == null) {
                return new ResponseEntity<AuthLoginResponse>(
                    new AuthLoginResponse(false, "Debe enviar x_correo y c_dni", null),
                    HttpStatus.UNAUTHORIZED
                );
            }

            Usuario usuario = usuarioService.authenticateByCorreoAndDni(
                request.getXCorreo(),
                request.getCDni()
            );

            if (usuario == null) {
                return new ResponseEntity<AuthLoginResponse>(
                    new AuthLoginResponse(false, "Credenciales inválidas o usuario inactivo", null),
                    HttpStatus.UNAUTHORIZED
                );
            }

            return new ResponseEntity<AuthLoginResponse>(
                new AuthLoginResponse(true, "Autenticación exitosa", usuario),
                HttpStatus.OK
            );
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }
}

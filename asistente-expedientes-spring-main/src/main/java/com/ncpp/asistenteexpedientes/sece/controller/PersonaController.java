package com.ncpp.asistenteexpedientes.sece.controller;

import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.impl.BitacoraServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
import com.ncpp.asistenteexpedientes.sece.entity.Persona;
import com.ncpp.asistenteexpedientes.sece.entity.Tipo;
import com.ncpp.asistenteexpedientes.sece.service.PersonaService;
import com.ncpp.asistenteexpedientes.util.Constants;
import com.ncpp.asistenteexpedientes.util.InternalServerException;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Personas", description = "API para validación y autenticación de usuarios ciudadanos")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET , RequestMethod.POST })
@RestController
@RequestMapping(Constants.VERSION_API+"/persona")
public class PersonaController {

    @Autowired
    PersonaService personaService;

    @Operation(
        summary = "Buscar y validar persona por DNI",
        description = "Valida un DNI y retorna los datos de la persona si está registrada en el sistema. "+
            "Registra el intento de login en bitácora tanto si es exitoso como fallido."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Persona encontrada y validada",
            content = @Content(schema = @Schema(implementation = Persona.class))
        ),
        @ApiResponse(responseCode = "404", description = "DNI no encontrado o inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Persona> find(
        @Parameter(description = "DNI de la persona a buscar", required = true, example = "12345678") @RequestParam String dni,
        @Parameter(description = "IP del módulo", required = true) @RequestParam String ipModulo, 
        @Parameter(description = "Usuario del módulo", required = true) @RequestParam String usuarioModulo){
        Persona persona = new Persona();
        Usuario usuario = null;
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
        Bitacora bitacora = new Bitacora();
        BitacoraServiceImpl bitacoraService = new BitacoraServiceImpl();
        try {
            // Validación principal contra nueva BD refactorizada (seg_usuario)
            usuario = usuarioService.findByDni(dni);
            if (usuario == null || usuario.getNIdTipo() == null || !"S".equalsIgnoreCase(usuario.getLActivo())) {
                throw new NotFoundException();
            }

            // Construir respuesta compatible con frontend actual (Persona + Tipo)
            persona.setDni(usuario.getCDni());
            persona.setNombre(usuario.getNombreCompleto());
            persona.setCorreo(usuario.getXCorreo());
            persona.setNumero(usuario.getCTelefono());
            persona.setEstado("S".equalsIgnoreCase(usuario.getLActivo()) ? 1 : 0);
            persona.setNIdUsuario(usuario.getNIdUsuario());

            Tipo tipo = new Tipo();
            tipo.setIdTipo(usuario.getNIdTipo());
            tipo.setNombre(obtenerNombreTipoUsuario(usuario.getNIdTipo()));
            persona.setTipo(tipo);

            // Campos legacy opcionales: intentar enriquecer desde SECE si existe
            try {
                Persona personaSece = personaService.buscarPorDni(dni);
                if (personaSece != null && personaSece.getIdPersona() > 0) {
                    persona.setIdPersona(personaSece.getIdPersona());
                }
            } catch (Exception ignored) {
                // No bloquea el login por lectora
            }
        } catch (Exception e) {
            System.out.println(e);			
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            if (e instanceof NotFoundException) {
                throw (NotFoundException) e;
            }
            throw new InternalServerException();  
        }

        bitacora.setNIdUsuario(usuario.getNIdUsuario());
        bitacora.setCCodigoAccion("LOGIN_PERSONA");
        bitacora.setTDescripcionAccion("LA PERSONA "+persona.getNombre()+" ("+persona.getDni()+") INGRESO AL ASISTENTE CON EXITO");
        bitacora.setCIpModulo(ipModulo);
        bitacora.setDniSece(dni);
        bitacora.setNombreSece(persona.getNombre());
        bitacoraService.create(bitacora);
        
        return new ResponseEntity<Persona>(persona,HttpStatus.OK);
    }

    private String obtenerNombreTipoUsuario(Integer nIdTipo) {
        if (nIdTipo == null) {
            return "Sin tipo";
        }
        switch (nIdTipo) {
            case 1: return "Administrador";
            case 2: return "Fiscal Provincial PE";
            case 3: return "Fiscal Adjunto Penal";
            case 4: return "Asistente de Fiscal";
            case 5: return "Defensor Publico";
            case 6: return "Procuraduria";
            case 7: return "Abogados (a)";
            case 8: return "Parte del Proceso";
            case 9: return "Invitado";
            case 10: return "CEM";
            default: return "Tipo " + nIdTipo;
        }
    }



    
}

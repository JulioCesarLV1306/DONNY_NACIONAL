package com.ncpp.asistenteexpedientes.asistente.controller;

import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.entity.Encuesta;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.BitacoraService;
import com.ncpp.asistenteexpedientes.asistente.service.EncuestaService;
import com.ncpp.asistenteexpedientes.asistente.service.impl.BitacoraServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.EncuestaServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Encuestas", description = "API para gestión de encuestas de satisfacción del usuario")
@CrossOrigin(origins = "*", methods = { RequestMethod.POST })
@RestController
@RequestMapping(Constants.VERSION_API+"/encuesta")
public class EncuestaController {

    EncuestaService encuestaService;
    BitacoraService bitacoraService;
    UsuarioServiceImpl usuarioService;
    
    public EncuestaController(){
        encuestaService = new EncuestaServiceImpl();
        bitacoraService = new BitacoraServiceImpl();
        usuarioService = new UsuarioServiceImpl();
    }

    @Operation(
        summary = "Crear encuesta de satisfacción",
        description = "Registra una nueva encuesta de satisfacción del usuario. "+
            "Si la encuesta tiene campos deprecados (dniSece, nombreSece), automáticamente busca o crea el usuario y establece nIdUsuario."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Encuesta creada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/crear", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void crear(
        @Parameter(description = "Datos de la encuesta", required = true, schema = @Schema(implementation = Encuesta.class))
        @RequestBody Encuesta encuesta, 
        @Parameter(description = "IP del módulo donde se realizó la encuesta", required = true)
        @RequestParam String ipModulo, 
        @Parameter(description = "Usuario del módulo", required = true)
        @RequestParam String usuarioModulo){
        try {
            // Obtener o crear usuario si viene con campos deprecated
            if (encuesta.getNIdUsuario() == null && encuesta.getDniSece() != null) {
                Usuario usuario = usuarioService.createIfNotExists(
                    encuesta.getDniSece(), 
                    encuesta.getNombreSece()
                );
                encuesta.setNIdUsuario(usuario.getNIdUsuario());
            }
            
            encuestaService.create(encuesta);
            
            // Registrar en bitácora con nuevo esquema
            Bitacora bitacora = new Bitacora();
            bitacora.setCCodigoAccion("ENCUESTA");
            
            String nombreCompleto = encuesta.getNombreSece() != null ? 
                encuesta.getNombreSece() : "Usuario";
            bitacora.setTDescripcionAccion(nombreCompleto + 
                " REGISTRO ENCUESTA CON CALIFICACION " + encuesta.getNCalificacion());
            
            bitacora.setNIdUsuario(encuesta.getNIdUsuario());
            bitacora.setDniSece(encuesta.getDniSece());
            bitacora.setNombreSece(encuesta.getNombreSece());
            bitacora.setCIpModulo(ipModulo);
            
            bitacoraService.create(bitacora);
        } catch (Exception e) {
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
        }
    }

    @Operation(
        summary = "Buscar encuesta por DNI",
        description = "Busca la última encuesta registrada por un usuario según su DNI"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Encuesta encontrada",
            content = @Content(schema = @Schema(implementation = Encuesta.class))
        ),
        @ApiResponse(responseCode = "404", description = "Encuesta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Encuesta>  buscar(
        @Parameter(description = "DNI del usuario", required = true, example = "12345678")
        @RequestParam String dni){
        Encuesta encuesta=null;
        try {
            encuesta=encuestaService.findByDni(dni);
        } catch (Exception e) {
            System.out.println(e);	
            e.printStackTrace();	
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);	
            throw new InternalServerException();  
        }
        if(encuesta==null) {  throw new NotFoundException();   }
        return new ResponseEntity<Encuesta>(encuesta,HttpStatus.OK);
    }
    
}

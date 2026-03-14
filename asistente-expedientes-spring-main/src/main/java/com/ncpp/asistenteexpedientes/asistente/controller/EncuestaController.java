package com.ncpp.asistenteexpedientes.asistente.controller;

import java.util.HashMap;
import java.util.Map;

import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.entity.Encuesta;
import com.ncpp.asistenteexpedientes.asistente.entity.Modulo;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.BitacoraService;
import com.ncpp.asistenteexpedientes.asistente.service.EncuestaService;
import com.ncpp.asistenteexpedientes.asistente.service.impl.BitacoraServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.EncuestaServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.ModuloServiceImpl;
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
    ModuloServiceImpl moduloService;
    
    public EncuestaController(){
        encuestaService = new EncuestaServiceImpl();
        bitacoraService = new BitacoraServiceImpl();
        usuarioService = new UsuarioServiceImpl();
        moduloService = new ModuloServiceImpl();
    }

    @Operation(
        summary = "Crear encuesta de satisfacción",
        description = "Registra una nueva encuesta de satisfacción del usuario. "+
            "Si la encuesta tiene campos deprecados (dniSece, nombreSece), automáticamente busca o crea el usuario y establece nIdUsuario."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Encuesta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos para registrar encuesta"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/crear", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> crear(
        @Parameter(description = "Datos de la encuesta", required = true, schema = @Schema(implementation = Encuesta.class))
        @RequestBody Encuesta encuesta, 
        @Parameter(description = "IP del módulo donde se realizó la encuesta", required = true)
        @RequestParam String ipModulo, 
        @Parameter(description = "Usuario del módulo", required = true)
        @RequestParam String usuarioModulo){
        try {
            Usuario usuarioEncuesta = null;

            // Resolver módulo automáticamente por IP cuando no llega en el body o llega como 0
            if ((encuesta.getNIdModulo() == null || encuesta.getNIdModulo() <= 0)
                && ipModulo != null && !ipModulo.trim().isEmpty()) {
                Modulo modulo = moduloService.findByIp(ipModulo.trim());
                if (modulo != null) {
                    encuesta.setNIdModulo(modulo.getNIdModulo());
                }
            }

            // Obtener o crear usuario si viene con campos deprecated
            if (encuesta.getNIdUsuario() == null && encuesta.getDniSece() != null) {
                String dniEncuesta = encuesta.getDniSece().trim();
                if (dniEncuesta.isEmpty()) {
                    throw new IllegalArgumentException("dniSece no puede estar vacío cuando n_id_usuario no es enviado.");
                }

                // Primero intentar encontrar usuario existente
                usuarioEncuesta = usuarioService.findByDni(dniEncuesta);

                // Si no existe, crear usuario invitado con nombre de respaldo
                if (usuarioEncuesta == null) {
                    String nombreEncuesta = encuesta.getNombreSece();
                    if (nombreEncuesta == null || nombreEncuesta.trim().isEmpty()) {
                        nombreEncuesta = "USUARIO " + dniEncuesta;
                    }
                    usuarioEncuesta = usuarioService.createIfNotExists(dniEncuesta, nombreEncuesta);
                }

                encuesta.setNIdUsuario(usuarioEncuesta.getNIdUsuario());
            }

            // Enriquecer datos de la persona que respondió con seg_usuario
            if (usuarioEncuesta == null && encuesta.getNIdUsuario() != null) {
                usuarioEncuesta = usuarioService.findById(encuesta.getNIdUsuario());
            }
            if (usuarioEncuesta == null && encuesta.getDniSece() != null) {
                usuarioEncuesta = usuarioService.findByDni(encuesta.getDniSece());
                if (usuarioEncuesta != null && encuesta.getNIdUsuario() == null) {
                    encuesta.setNIdUsuario(usuarioEncuesta.getNIdUsuario());
                }
            }

            if (encuesta.getNIdUsuario() == null) {
                throw new IllegalArgumentException(
                    "No se pudo identificar al usuario de la encuesta. Envíe n_id_usuario o dniSece válido en seg_usuario."
                );
            }

            if (usuarioEncuesta != null) {
                if (encuesta.getDniSece() == null || encuesta.getDniSece().trim().isEmpty()) {
                    encuesta.setDniSece(usuarioEncuesta.getCDni());
                }
                if (encuesta.getNombreSece() == null || encuesta.getNombreSece().trim().isEmpty()) {
                    encuesta.setNombreSece(usuarioEncuesta.getNombreCompleto());
                }
            }

            if (encuesta.getNIdModulo() == null || encuesta.getNIdModulo() <= 0) {
                throw new IllegalArgumentException(
                    "n_id_modulo es obligatorio. Envíelo en el body o asegure que ipModulo exista en seg_modulo."
                );
            }
            
            encuestaService.create(encuesta);
            
            // Registrar en bitácora con nuevo esquema
            Bitacora bitacora = new Bitacora();
            bitacora.setCCodigoAccion("ENCUESTA");
            
            String nombreCompleto = encuesta.getNombreSece() != null ? 
                encuesta.getNombreSece() : (usuarioEncuesta != null ? usuarioEncuesta.getNombreCompleto() : "Usuario");
            bitacora.setTDescripcionAccion(nombreCompleto + 
                " REGISTRO ENCUESTA CON CALIFICACION " + encuesta.getNCalificacion());
            
            bitacora.setNIdUsuario(encuesta.getNIdUsuario());
            bitacora.setDniSece(encuesta.getDniSece());
            bitacora.setNombreSece(nombreCompleto);
            bitacora.setCIpModulo(ipModulo);
            
            bitacoraService.create(bitacora);

            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("mensaje", "Encuesta registrada correctamente");
            response.put("nIdUsuario", encuesta.getNIdUsuario());
            response.put("dni", encuesta.getDniSece());
            response.put("nombre", nombreCompleto);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            LogDony.write(this.getClass().getName()+" - VALIDATION ERROR: "+e);
            Map<String, Object> response = new HashMap<>();
            response.put("ok", false);
            response.put("mensaje", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            Map<String, Object> response = new HashMap<>();
            response.put("ok", false);
            response.put("mensaje", "Error interno al registrar encuesta");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

package com.ncpp.asistenteexpedientes.asistente.controller;

import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.payload.request.RequestBitacora;
import com.ncpp.asistenteexpedientes.asistente.service.impl.BitacoraServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
import com.ncpp.asistenteexpedientes.util.Constants;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bitácora", description = "API para registro de auditoría y seguimiento de eventos del sistema")
@CrossOrigin(origins = "*", methods = { RequestMethod.PUT })
@RestController
@RequestMapping(Constants.VERSION_API+"/bitacora")
public class BitacoraController {
    BitacoraServiceImpl bitacoraService;
    UsuarioServiceImpl usuarioService;
    
    public BitacoraController(){
        bitacoraService = new BitacoraServiceImpl();
        usuarioService = new UsuarioServiceImpl();
    }

    @Operation(
        summary = "Registrar error en bitácora",
        description = "Registra un evento de error en la bitácora del sistema con información del usuario y módulo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Error registrado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public int error(
        @Parameter(description = "Información del error a registrar", required = true)
        @RequestBody RequestBitacora requestBitacora){
        try{
            Bitacora bitacora = new Bitacora();
            bitacora.setCCodigoAccion("ERROR_"+requestBitacora.getCodigo().replace(" ", "_"));
            bitacora.setTDescripcionAccion("SE DETECTO UN ERROR CON CODIGO "+requestBitacora.getCodigo());
            
            // Obtener o crear usuario y establecer nIdUsuario (nuevo esquema)
            try {
                String dni = requestBitacora.getPersona().getDni();
                String nombre = requestBitacora.getPersona().getNombre();
                Usuario usuario = usuarioService.createIfNotExists(dni, nombre);
                bitacora.setNIdUsuario(usuario.getNIdUsuario());
                
                // Mantener campos deprecated para compatibilidad
                bitacora.setDniSece(dni);
                bitacora.setNombreSece(nombre);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }

            try {
                bitacora.setCIpModulo(requestBitacora.getModulo().getCPcIp());
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }
            
            bitacoraService.create(bitacora);

            return HttpStatus.OK.value();
        } catch(Exception e){
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }

    @Operation(
        summary = "Crear registro en bitácora",
        description = "Registra una acción del usuario en la bitácora del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro creado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public int create(
        @Parameter(description = "Información de la acción a registrar", required = true)
        @RequestBody RequestBitacora requestBitacora){
        try{
            Bitacora bitacora = new Bitacora();
            bitacora.setCCodigoAccion(requestBitacora.getCodigo());
            bitacora.setTDescripcionAccion(requestBitacora.getDescripcion());
            
            // Obtener o crear usuario y establecer nIdUsuario (nuevo esquema)
            try {
                String dni = requestBitacora.getPersona().getDni();
                String nombre = requestBitacora.getPersona().getNombre();
                Usuario usuario = usuarioService.createIfNotExists(dni, nombre);
                bitacora.setNIdUsuario(usuario.getNIdUsuario());
                
                // Mantener campos deprecated para compatibilidad
                bitacora.setDniSece(dni);
                bitacora.setNombreSece(nombre);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }

            try {
                bitacora.setCIpModulo(requestBitacora.getModulo().getCPcIp());
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }
            
            bitacoraService.create(bitacora);

            return HttpStatus.OK.value();
        } catch(Exception e){
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }

    
}

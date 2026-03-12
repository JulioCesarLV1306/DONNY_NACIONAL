package com.ncpp.asistenteexpedientes.sece.controller;

import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.impl.BitacoraServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
import com.ncpp.asistenteexpedientes.sece.entity.Persona;
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
        try {
            persona=personaService.buscarPorDni(dni);
        } catch (Exception e) {
            System.out.println(e);			
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        Bitacora bitacora = new Bitacora();
        BitacoraServiceImpl bitacoraService = new BitacoraServiceImpl();
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
        
        if(persona!=null){
            // Obtener/crear usuario con nuevo esquema
            Usuario usuario = usuarioService.createIfNotExists(dni, persona.getNombre());
            
            // Usar nuevo esquema con nomenclatura refactorizada
            bitacora.setNIdUsuario(usuario.getNIdUsuario());      // ✅ FK obligatoria
            bitacora.setCCodigoAccion("LOGIN_PERSONA");           // ✅ c_ = código
            bitacora.setTDescripcionAccion("LA PERSONA "+persona.getNombre()+" ("+persona.getDni()+") INGRESO AL ASISTENTE CON EXITO");
            bitacora.setCIpModulo(ipModulo);                      // ✅ c_ = código
            
            // Mantener campos deprecated para compatibilidad
            bitacora.setDniSece(dni);
            bitacora.setNombreSece(persona.getNombre());
            
            bitacoraService.create(bitacora);
        }else{
            // Para login fallido, crear usuario temporal si no existe
            Usuario usuario = usuarioService.createIfNotExists(dni, "USUARIO_DESCONOCIDO");
            
            bitacora.setNIdUsuario(usuario.getNIdUsuario());      // ✅ FK obligatoria
            bitacora.setCCodigoAccion("FAIL_LOGIN_PERSONA");      // ✅ c_ = código
            bitacora.setTDescripcionAccion("EL DNI "+dni+" INTENTO INGRESAR AL ASISTENTE SIN EXITO");
            bitacora.setCIpModulo(ipModulo);                      // ✅ c_ = código
            
            // Mantener campos deprecated para compatibilidad
            bitacora.setDniSece(dni);
            
            bitacoraService.create(bitacora);
            throw new NotFoundException();  
        } 
        
        return new ResponseEntity<Persona>(persona,HttpStatus.OK);
    }



    
}

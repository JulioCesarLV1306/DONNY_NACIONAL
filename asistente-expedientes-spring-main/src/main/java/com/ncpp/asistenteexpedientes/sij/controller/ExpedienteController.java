package com.ncpp.asistenteexpedientes.sij.controller;

import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.impl.BitacoraServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.EstadisticasServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
import com.ncpp.asistenteexpedientes.sij.entity.Expediente;
import com.ncpp.asistenteexpedientes.sij.payload.request.RequestBusquedaExpediente;
import com.ncpp.asistenteexpedientes.sij.payload.response.ResponseEleccionModel;
import com.ncpp.asistenteexpedientes.sij.service.impl.ExpedienteServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Expedientes", description = "API para búsqueda y consulta de expedientes judiciales")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET , RequestMethod.POST })
@RestController
@RequestMapping(Constants.VERSION_API+"/expediente")
public class ExpedienteController {
    
    ExpedienteServiceImpl expedienteService;

    public ExpedienteController(){
        expedienteService = new ExpedienteServiceImpl();
    }

    @Operation(
        summary = "Buscar expedientes por número y año",
        description = "Busca expedientes judiciales según número, año, cuaderno y especialidad. "+
            "Solo busca expedientes desde el año 2014 en adelante. "+
            "Registra automáticamente la búsqueda en bitácora."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Expedientes encontrados",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "No se encontraron expedientes"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/buscar",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Expediente>> buscar(
        @Parameter(description = "Número del expediente", required = true, example = "12345") @RequestParam int numero, 
        @Parameter(description = "Año del expediente (mínimo 2014)", required = true, example = "2024") @RequestParam int anio, 
        @Parameter(description = "Número de cuaderno (-1 para todos)", required = true, example = "1") @RequestParam int cuaderno,  
        @Parameter(description = "Especialidad (CI=Civil, PE=Penal, LA=Laboral, FA=Familia)", required = true, example = "CI") @RequestParam String especialidad, 
        @Parameter(description = "DNI del usuario que realiza la consulta", required = true, example = "12345678") @RequestParam String dni,
        @Parameter(description = "Tipo de persona consultante", required = true, example = "1") @RequestParam int id_tipo, 
        @Parameter(description = "Nombre completo del usuario", required = true) @RequestParam String nombrePersona, 
        @Parameter(description = "IP del módulo desde donde se consulta", required = true) @RequestParam String ipModulo,
        @Parameter(description = "ID del módulo", required = true) @RequestParam long idModulo,
        @Parameter(description = "Usuario del módulo", required = true) @RequestParam String usuarioModulo, 
        Pageable pageable ){
        Page<Expediente> lista ; 
        try {
            if(anio< 2014 || numero <=0) throw new NotFoundException();  
            lista=expedienteService.buscar(new RequestBusquedaExpediente(numero, anio, cuaderno,especialidad, dni, id_tipo), pageable);
        } catch (Exception e) {
            System.out.println(e);		
            e.printStackTrace();	
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        if(lista!=null){
            // Obtener/crear usuario con nuevo esquema
            UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
            Usuario usuario = usuarioService.createIfNotExists(dni, nombrePersona);
            
            Bitacora bitacora = new Bitacora();
            BitacoraServiceImpl bitacoraService = new BitacoraServiceImpl();
            
            // Usar nuevo esquema con nomenclatura refactorizada
            bitacora.setNIdUsuario(usuario.getNIdUsuario());  // ✅ FK obligatoria
            bitacora.setCCodigoAccion("BUSQUEDA_EXPEDIENTE"); // ✅ c_ = código
            bitacora.setTDescripcionAccion("BUSCO EL EXPEDIENTE CON NUMERO "+numero+" AÑO "+anio+" CUADERNO "+cuaderno
                +" EN LA ESPECIALIDAD "+especialidad+ " Y OBTUVO "+lista.getTotalElements()+ " RESULTADOS");
            bitacora.setCIpModulo(ipModulo);                  // ✅ c_ = código
            
            // Mantener campos deprecated para compatibilidad
            bitacora.setDniSece(dni);
            bitacora.setNombreSece(nombrePersona);
            
            bitacoraService.create(bitacora);

            aumentarEspecialidad(idModulo, especialidad);

        }else{
            throw new NotFoundException();  
        } 
        return new ResponseEntity<Page<Expediente>>(lista,HttpStatus.OK);
    }

    @Operation(
        summary = "Buscar expedientes por DNI",
        description = "Busca todos los expedientes asociados a un DNI específico en una especialidad. "+
            "Registra automáticamente la búsqueda en bitácora."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Expedientes encontrados",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "No se encontraron expedientes para el DNI"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/buscar/dni",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Expediente>> buscarDNI(
        @Parameter(description = "DNI a consultar", required = true, example = "12345678") @RequestParam String dni,  
        @Parameter(description = "Especialidad (CI=Civil, PE=Penal, LA=Laboral, FA=Familia)", required = true, example = "CI") @RequestParam String especialidad, 
        @Parameter(description = "Tipo de persona consultante", required = true, example = "1") @RequestParam int id_tipo, 
        @Parameter(description = "Nombre completo del usuario", required = true) @RequestParam String nombrePersona, 
        @Parameter(description = "IP del módulo desde donde se consulta", required = true) @RequestParam String ipModulo, 
        @Parameter(description = "ID del módulo", required = true) @RequestParam long idModulo,
        @Parameter(description = "Usuario del módulo", required = true) @RequestParam String usuarioModulo, 
        Pageable pageable ){
        Page<Expediente> lista ; 
        try {
            lista=expedienteService.buscarPorDNI(dni,especialidad, pageable);
        } catch (Exception e) {
            System.out.println(e);		
            e.printStackTrace();	
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        if(lista!=null){
            // Obtener/crear usuario con nuevo esquema
            UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
            Usuario usuario = usuarioService.createIfNotExists(dni, nombrePersona);
            
            Bitacora bitacora = new Bitacora();
            BitacoraServiceImpl bitacoraService = new BitacoraServiceImpl();
            
            // Usar nuevo esquema con nomenclatura refactorizada
            bitacora.setNIdUsuario(usuario.getNIdUsuario());  // ✅ FK obligatoria
            bitacora.setCCodigoAccion("BUSQUEDA_EXPEDIENTE"); // ✅ c_ = código
            bitacora.setTDescripcionAccion("LA PERSONA DE DNI "+dni
                +" BUSCO EN LA ESPECIALIDAD "+especialidad+ " Y OBTUVO "+lista.getNumberOfElements()+  " ELEMENTOS DE " + lista.getTotalElements() + " PAGINA "+ pageable.getPageNumber());
            bitacora.setCIpModulo(ipModulo);                  // ✅ c_ = código
            
            // Mantener campos deprecated para compatibilidad
            bitacora.setDniSece(dni);
            bitacora.setNombreSece(nombrePersona);
            
            bitacoraService.create(bitacora);

            aumentarEspecialidad(idModulo, especialidad);
        }else{
            throw new NotFoundException();  
        } 
        return new ResponseEntity<Page<Expediente>>(lista,HttpStatus.OK);
    }

    @Operation(
        summary = "Contar documentos disponibles",
        description = "Obtiene conteos de documentos digitalizados, videos, resoluciones y actas disponibles para un expediente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteos obtenidos exitosamente",
            content = @Content(schema = @Schema(implementation = ResponseEleccionModel.class))
        ),
        @ApiResponse(responseCode = "404", description = "No se encontraron documentos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/contar",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResponseEleccionModel>> getConteos(
        @Parameter(description = "Número único del expediente", required = true, example = "00123-2024-CI") @RequestParam String nUnico, 
        @Parameter(description = "Número de incidente", required = true, example = "1") @RequestParam int nIncidente ){
        List<ResponseEleccionModel> lista=null ; 
        try {
           lista=expedienteService.getConteos( nUnico, nIncidente);
        } catch (Exception e) {
            System.out.println(e);			
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        if(lista==null) throw new NotFoundException();  


        return new ResponseEntity<List<ResponseEleccionModel>>(lista,HttpStatus.OK);
    }

    private void aumentarEspecialidad(long idModulo, String especialidad){
        EstadisticasServiceImpl estadisticasServiceImpl = new EstadisticasServiceImpl();
        switch (especialidad) {
            case "LA":
                estadisticasServiceImpl.aumentarLaboral(idModulo, 1);
                break;
            case "CI":
                estadisticasServiceImpl.aumentarCivil(idModulo, 1);
                break;
            case "FA":
                estadisticasServiceImpl.aumentarFamilia(idModulo, 1);
                break;
            case "PE":
                estadisticasServiceImpl.aumentarPenal(idModulo, 1);
                break;
            case "TODOS":
                estadisticasServiceImpl.aumentarLaboral(idModulo, 1);
                estadisticasServiceImpl.aumentarCivil(idModulo, 1);
                estadisticasServiceImpl.aumentarFamilia(idModulo, 1);
                estadisticasServiceImpl.aumentarPenal(idModulo, 1);
                break;
        }
    }
    
}

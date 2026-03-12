package com.ncpp.asistenteexpedientes.sij.controller;

import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.dto.Drive;
import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.entity.Descarga;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.impl.BitacoraServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
import com.ncpp.asistenteexpedientes.sij.payload.response.ResponseTamanioDescarga;
import com.ncpp.asistenteexpedientes.sij.service.impl.DownloaderServiceImpl;
import com.ncpp.asistenteexpedientes.sij.service.specs.DownloaderService;
import com.ncpp.asistenteexpedientes.util.Constants;
import com.ncpp.asistenteexpedientes.util.InternalServerException;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET , RequestMethod.POST })
@RestController
@RequestMapping(Constants.VERSION_API+"/downloader")
public class DownloaderController {

    private DownloaderService downloaderService;

    public DownloaderController(){
        downloaderService= new DownloaderServiceImpl();
    }

    @PutMapping(value = "/tamanio",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResponseTamanioDescarga>> size( @RequestParam String nUnico, @RequestParam int nIncidente, 
  @RequestParam String []fechas, @RequestParam String []elecciones ){
        List<ResponseTamanioDescarga> lista=null ; 
        try {
           lista=downloaderService.getTamanioDescarga(nUnico, nIncidente,fechas,elecciones);
        } catch (Exception e) {
            System.out.println(e);			
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        if(lista==null) throw new NotFoundException();  

        return new ResponseEntity<List<ResponseTamanioDescarga>>(lista,HttpStatus.OK);
    }


    @PutMapping(value = "/descarga",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Descarga> descarga( @RequestParam String expediente, @RequestParam String nUnico,@RequestParam int nIncidente, 
    @RequestParam String []fechas, @RequestParam String eleccion, @RequestParam long tamanio,
     @RequestParam String ipModulo, @RequestParam String usuarioModulo, @RequestParam String dniPersona, 
     @RequestParam String nombrePersona, @RequestParam long idModulo, @RequestBody Drive drive){
        Descarga response=null ; 
        try {
           response=downloaderService.descargar(expediente,nUnico, nIncidente,fechas,eleccion,tamanio, drive,ipModulo,idModulo, dniPersona);
        } catch (Exception e) {
            System.out.println(e);		
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);	
            throw new InternalServerException();  
        }
        if(response!=null){
            // Obtener/crear usuario con nuevo esquema
            UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
            Usuario usuario = usuarioService.createIfNotExists(dniPersona, nombrePersona);
            
            Bitacora bitacora = new Bitacora();
            BitacoraServiceImpl bitacoraService = new BitacoraServiceImpl();
            
            // Usar nuevo esquema con nomenclatura refactorizada
            bitacora.setNIdUsuario(usuario.getNIdUsuario());  // ✅ FK obligatoria
            bitacora.setCCodigoAccion("DESCARGA_ARCHIVOS");   // ✅ c_ = código
            bitacora.setTDescripcionAccion("EMPEZO SU PROCESO DE DESCARGA "
                + " EN EL DISPOSITIVO "+ drive.getTipo().toUpperCase()+" "+drive.getNombre()
                + " DEL EXPEDIENTE " +expediente+" LOS ARCHIVOS "+eleccion.toUpperCase()+" CON FECHAS "+String.join(", ", fechas));
            bitacora.setCIpModulo(ipModulo);                  // ✅ c_ = código
            
            // Mantener campos deprecated para compatibilidad
            bitacora.setDniSece(dniPersona);
            bitacora.setNombreSece(nombrePersona);
            
            bitacoraService.create(bitacora);
        }else{
            throw new NotFoundException();  
        } 

        return new ResponseEntity<Descarga>(response,HttpStatus.OK);
    }

    @PutMapping(value = "/consultar",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Descarga> consultar( @RequestParam String nUnico,@RequestParam int nIncidente, @RequestParam String eleccion,
    @RequestParam String ipModulo, @RequestParam String dniPersona, @RequestParam String []fechas){
        Descarga response=null ; 
        try {
           response=downloaderService.consultar(ipModulo, dniPersona, nUnico,nIncidente, eleccion, fechas);
        } catch (Exception e) {
            System.out.println(e);			
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        if(response==null) throw new NotFoundException();  

        return new ResponseEntity<Descarga>(response,HttpStatus.OK);
    }
/*
    @PutMapping(value = "/copiar",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseEstadoDescarga> copiar( @RequestParam String nUnico ,@RequestParam int nIncidente, @RequestParam String eleccion,
 @RequestParam String dniPersona, @RequestParam String []fechas, @RequestBody RequestCopiar requestCopiar){
        ResponseEstadoDescarga response=null ; 
        try {
           response=downloaderService.copiar(nUnico,nIncidente, fechas, eleccion, requestCopiar.getDrive(), requestCopiar.getModulo(), dniPersona);
        } catch (Exception e) {
            System.out.println(e);		
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);	
            throw new InternalServerException();  
        }
        if(response==null) throw new NotFoundException();   

        return new ResponseEntity<ResponseEstadoDescarga>(response,HttpStatus.OK);
    }*/

   /*@PutMapping(value="/olvidar",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> olvidar( @RequestParam String ipModulo, @RequestParam String dniPersona, @RequestParam String nUnico,
    @RequestParam int nIncidente,@RequestParam String eleccion, @RequestParam String []fechas ){
        try {
            downloaderService.olvidar(ipModulo, dniPersona, nUnico, nIncidente, eleccion, fechas);
        } catch (Exception e) {
            System.out.println(e);
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
        }
        return new ResponseEntity<>("{}",HttpStatus.OK);
    }*/



    
}

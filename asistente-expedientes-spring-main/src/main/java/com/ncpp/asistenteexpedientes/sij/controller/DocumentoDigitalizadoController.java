package com.ncpp.asistenteexpedientes.sij.controller;

import java.util.List;

import com.ncpp.asistenteexpedientes.sij.entity.Archivo;
import com.ncpp.asistenteexpedientes.sij.entity.Fecha;
import com.ncpp.asistenteexpedientes.sij.service.impl.DocumentoDigitalizadoServiceImpl;
import com.ncpp.asistenteexpedientes.util.Constants;
import com.ncpp.asistenteexpedientes.util.InternalServerException;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.NotFoundException;


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

@CrossOrigin(origins = "*", methods = { RequestMethod.GET , RequestMethod.POST })
@RestController
@RequestMapping(Constants.VERSION_API+"/digitalizados")
public class DocumentoDigitalizadoController {

    private DocumentoDigitalizadoServiceImpl documentoDigitalizadoService;
    public DocumentoDigitalizadoController(){
        documentoDigitalizadoService = new DocumentoDigitalizadoServiceImpl();
    }

    @GetMapping(value = "/buscar",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Archivo>> buscarTodo(@RequestParam String nUnico,@RequestParam int nIncidente ){
        List<Archivo> lista=null ; 
        try {
           lista=documentoDigitalizadoService.buscarTodo(nUnico, nIncidente);
        } catch (Exception e) {
            System.out.println(e);			
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        if(lista==null) throw new NotFoundException();  

        return new ResponseEntity<List<Archivo>>(lista,HttpStatus.OK);
    }

    @GetMapping(value = "/fechas",  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Fecha>> getFechas(@RequestParam String nUnico,@RequestParam int nIncidente,Pageable page ){
        Page<Fecha> lista=null ; 
        try {
           lista=documentoDigitalizadoService.getFechas(nUnico, nIncidente,page);
        } catch (Exception e) {
            System.out.println(e);			
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            throw new InternalServerException();  
        }
        if(lista==null) throw new NotFoundException();  

        return new ResponseEntity<Page<Fecha>>(lista,HttpStatus.OK);
    }
    
}

package com.ncpp.asistenteexpedientes.asistente.controller;

import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.entity.Modulo;
import com.ncpp.asistenteexpedientes.asistente.service.impl.ModuloServiceImpl;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Módulos", description = "API CRUD para gestión de módulos/PCs registrados en seg_modulo")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(Constants.VERSION_API + "/modulo")
public class ModuloController {

    private ModuloServiceImpl moduloService;

    public ModuloController() {
        moduloService = new ModuloServiceImpl();
    }

    @Operation(
        summary = "Listar módulos",
        description = "Retorna todos los módulos registrados en seg_modulo"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado obtenido correctamente",
            content = @Content(schema = @Schema(implementation = Modulo.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/listar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Modulo>> listar() {
        try {
            return new ResponseEntity<List<Modulo>>(moduloService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Obtener módulo por ID",
        description = "Busca un módulo específico según su n_id_modulo"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Módulo encontrado",
            content = @Content(schema = @Schema(implementation = Modulo.class))
        ),
        @ApiResponse(responseCode = "404", description = "Módulo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtener/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Modulo> obtenerPorId(
        @Parameter(description = "ID del módulo", required = true, example = "1")
        @PathVariable("id") Long nIdModulo) {
        try {
            Modulo modulo = moduloService.findById(nIdModulo);
            if (modulo == null) {
                throw new NotFoundException();
            }
            return new ResponseEntity<Modulo>(modulo, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Buscar módulo por IP",
        description = "Busca un módulo por su dirección IP"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Módulo encontrado",
            content = @Content(schema = @Schema(implementation = Modulo.class))
        ),
        @ApiResponse(responseCode = "404", description = "Módulo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Modulo> buscarPorIp(
        @Parameter(description = "IP del módulo", required = true, example = "192.168.1.100")
        @RequestParam String ip) {
        try {
            Modulo modulo = moduloService.findByIp(ip);
            if (modulo == null) {
                throw new NotFoundException();
            }
            return new ResponseEntity<Modulo>(modulo, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Crear módulo",
        description = "Crea un nuevo módulo en seg_modulo"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Módulo creado correctamente",
            content = @Content(schema = @Schema(implementation = Modulo.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/crear", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Modulo> crear(
        @Parameter(description = "Datos del módulo a crear", required = true, schema = @Schema(implementation = Modulo.class))
        @RequestBody Modulo modulo) {
        try {
            Modulo creado = moduloService.create(modulo);
            return new ResponseEntity<Modulo>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Actualizar módulo",
        description = "Actualiza un módulo existente según su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Módulo actualizado correctamente",
            content = @Content(schema = @Schema(implementation = Modulo.class))
        ),
        @ApiResponse(responseCode = "404", description = "Módulo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/actualizar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Modulo> actualizar(
        @Parameter(description = "ID del módulo a actualizar", required = true, example = "1")
        @PathVariable("id") Long nIdModulo,
        @Parameter(description = "Datos actualizados del módulo", required = true, schema = @Schema(implementation = Modulo.class))
        @RequestBody Modulo modulo) {
        try {
            Modulo actualizado = moduloService.update(nIdModulo, modulo);
            if (actualizado == null) {
                throw new NotFoundException();
            }
            return new ResponseEntity<Modulo>(actualizado, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Eliminar módulo",
        description = "Elimina un módulo existente según su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Módulo eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Módulo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping(value = "/eliminar/{id}")
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID del módulo a eliminar", required = true, example = "1")
        @PathVariable("id") Long nIdModulo) {
        try {
            boolean eliminado = moduloService.delete(nIdModulo);
            if (!eliminado) {
                throw new NotFoundException();
            }
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }
}

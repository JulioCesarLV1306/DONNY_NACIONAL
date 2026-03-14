package com.ncpp.asistenteexpedientes.asistente.controller;

import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
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

@Tag(name = "Usuarios", description = "API CRUD para gestión de usuarios del sistema")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(Constants.VERSION_API + "/usuario")
public class UsuarioController {

    private UsuarioServiceImpl usuarioService;

    public UsuarioController() {
        usuarioService = new UsuarioServiceImpl();
    }

    @Operation(
        summary = "Listar usuarios",
        description = "Retorna todos los usuarios registrados en seg_usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado obtenido correctamente",
            content = @Content(schema = @Schema(implementation = Usuario.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/listar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Usuario>> listar() {
        try {
            return new ResponseEntity<List<Usuario>>(usuarioService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Busca un usuario específico según su n_id_usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = Usuario.class))
        ),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtener/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> obtenerPorId(
        @Parameter(description = "ID del usuario", required = true, example = "1")
        @PathVariable("id") Long nIdUsuario) {
        try {
            Usuario usuario = usuarioService.findById(nIdUsuario);
            if (usuario == null) {
                throw new NotFoundException();
            }
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
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
        summary = "Buscar usuario por DNI",
        description = "Busca un usuario por su número de DNI"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = Usuario.class))
        ),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> buscarPorDni(
        @Parameter(description = "DNI del usuario", required = true, example = "12345678")
        @RequestParam String dni) {
        try {
            Usuario usuario = usuarioService.findByDni(dni);
            if (usuario == null) {
                throw new NotFoundException();
            }
            return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
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
        summary = "Crear usuario",
        description = "Crea un nuevo usuario en seg_usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado correctamente",
            content = @Content(schema = @Schema(implementation = Usuario.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/crear", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> crear(
        @Parameter(description = "Datos del usuario a crear", required = true, schema = @Schema(implementation = Usuario.class))
        @RequestBody Usuario usuario) {
        try {
            Usuario creado = usuarioService.create(usuario);
            return new ResponseEntity<Usuario>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza un usuario existente según su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario actualizado correctamente",
            content = @Content(schema = @Schema(implementation = Usuario.class))
        ),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/actualizar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Usuario> actualizar(
        @Parameter(description = "ID del usuario a actualizar", required = true, example = "1")
        @PathVariable("id") Long nIdUsuario,
        @Parameter(description = "Datos actualizados del usuario", required = true, schema = @Schema(implementation = Usuario.class))
        @RequestBody Usuario usuario) {
        try {
            Usuario actualizado = usuarioService.update(nIdUsuario, usuario);
            if (actualizado == null) {
                throw new NotFoundException();
            }
            return new ResponseEntity<Usuario>(actualizado, HttpStatus.OK);
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
        summary = "Eliminar usuario",
        description = "Elimina un usuario existente según su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping(value = "/eliminar/{id}")
    public ResponseEntity<Void> eliminar(
        @Parameter(description = "ID del usuario a eliminar", required = true, example = "1")
        @PathVariable("id") Long nIdUsuario) {
        try {
            boolean eliminado = usuarioService.delete(nIdUsuario);
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

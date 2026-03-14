package com.ncpp.asistenteexpedientes.asistente.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.entity.Estadisticas;
import com.ncpp.asistenteexpedientes.asistente.service.impl.EstadisticasServiceImpl;
import com.ncpp.asistenteexpedientes.util.Constants;
import com.ncpp.asistenteexpedientes.util.InternalServerException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Estadísticas", description = "API para consulta de métricas documentales (met_estadistica)")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(Constants.VERSION_API + "/estadistica")
public class EstadisticasController {

    private EstadisticasServiceImpl estadisticasService;

    public EstadisticasController() {
        estadisticasService = new EstadisticasServiceImpl();
    }

    @Operation(
        summary = "Ver estadísticas del día por módulo",
        description = "Retorna las estadísticas documentales del día actual para un módulo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Consulta realizada correctamente",
            content = @Content(schema = @Schema(implementation = Estadisticas.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/hoy/{idModulo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Estadisticas> verEstadisticasHoyPorModulo(
        @Parameter(description = "ID del módulo", required = true, example = "1")
        @PathVariable("idModulo") Long nIdModulo) {
        try {
            Estadisticas estadisticas = estadisticasService.obtenerEstadisticasHoy(nIdModulo);
            return new ResponseEntity<Estadisticas>(estadisticas, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Listar estadísticas del día",
        description = "Retorna el listado de estadísticas documentales registradas en la fecha actual"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado obtenido correctamente",
            content = @Content(schema = @Schema(implementation = Estadisticas.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/hoy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Estadisticas>> listarEstadisticasHoy() {
        try {
            return new ResponseEntity<List<Estadisticas>>(estadisticasService.listarEstadisticasHoy(), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }

    @Operation(
        summary = "Listar estadísticas por rango de fechas",
        description = "Retorna estadísticas históricas entre fechaInicio y fechaFin (incluye videos)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Listado histórico obtenido correctamente",
            content = @Content(schema = @Schema(implementation = Estadisticas.class))
        ),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/rango", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Estadisticas>> listarEstadisticasPorRango(
        @Parameter(description = "Fecha inicio (formato YYYY-MM-DD)", required = true, example = "2026-03-01")
        @RequestParam String fechaInicio,
        @Parameter(description = "Fecha fin (formato YYYY-MM-DD)", required = true, example = "2026-03-13")
        @RequestParam String fechaFin) {
        try {
            Date sqlFechaInicio = Date.valueOf(LocalDate.parse(fechaInicio));
            Date sqlFechaFin = Date.valueOf(LocalDate.parse(fechaFin));

            if (sqlFechaInicio.after(sqlFechaFin)) {
                return new ResponseEntity<List<Estadisticas>>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<List<Estadisticas>>(
                estadisticasService.listarEstadisticasPorRango(sqlFechaInicio, sqlFechaFin),
                HttpStatus.OK
            );
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            throw new InternalServerException();
        }
    }
}

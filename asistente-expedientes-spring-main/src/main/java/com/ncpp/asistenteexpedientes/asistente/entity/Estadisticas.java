package com.ncpp.asistenteexpedientes.asistente.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Estadisticas - Tabla met_estadistica (Métricas Documentales)
 * 
 * Registra estadísticas de documentos procesados por módulo.
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Estadísticas documentales por módulo y fecha (tabla met_estadistica)")
public class Estadisticas implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;

    @Schema(description = "ID único de la estadística", example = "10")
    private Long nIdEstadistica;

    @Schema(description = "ID del módulo asociado", example = "1")
    private Long nIdModulo;              // FK a seg_modulo

    @Schema(description = "Cantidad de actas procesadas", example = "15")
    private Integer nActas;

    @Schema(description = "Cantidad de resoluciones procesadas", example = "22")
    private Integer nResoluciones;

    @Schema(description = "Cantidad total de documentos procesados", example = "37")
    private Integer nDocumentos;

    @Schema(description = "Cantidad total de videos procesados", example = "9")
    private Integer nVideos;

    @Schema(description = "Cantidad total de hojas procesadas", example = "184")
    private Integer nHojas;

    @Schema(description = "Cantidad total de bytes descargados/procesados", example = "2560000")
    private Long nBytes;

    @Schema(description = "Cantidad de documentos del ámbito penal", example = "12")
    private Long nPenal;

    @Schema(description = "Cantidad de documentos del ámbito laboral", example = "8")
    private Long nLaboral;

    @Schema(description = "Cantidad de documentos del ámbito civil", example = "10")
    private Long nCivil;

    @Schema(description = "Cantidad de documentos del ámbito familia", example = "7")
    private Long nFamilia;

    @Schema(description = "Fecha de la estadística", example = "2026-03-13")
    private Date fFecha;
    
    // Campos de auditoría
    @Schema(description = "Fecha de auditoría", accessMode = Schema.AccessMode.READ_ONLY)
    private Date fAud;

    @Schema(description = "Bandera de auditoría (I/U/D)", accessMode = Schema.AccessMode.READ_ONLY)
    private String bAud;
    
}

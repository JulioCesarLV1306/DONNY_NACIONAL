package com.ncpp.asistenteexpedientes.asistente.entity;

import java.io.Serializable;
import java.util.Date;

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
public class Estadisticas implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;

    private Long nIdEstadistica;
    private Long nIdModulo;              // FK a seg_modulo
    private Integer nActas;
    private Integer nResoluciones;
    private Integer nDocumentos;
    private Integer nHojas;
    private Long nBytes;
    private Long nPenal;
    private Long nLaboral;
    private Long nCivil;
    private Long nFamilia;
    private Date fFecha;
    
    // Campos de auditoría
    private Date fAud;
    private String bAud;
    
    // Campo deprecado
    @Deprecated
    private Integer videos;

}

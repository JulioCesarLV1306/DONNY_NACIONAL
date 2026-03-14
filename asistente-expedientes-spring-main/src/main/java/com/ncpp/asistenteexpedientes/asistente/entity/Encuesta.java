package com.ncpp.asistenteexpedientes.asistente.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Encuesta - Tabla met_encuesta (Satisfacción de Usuario)
 * 
 * Registra las encuestas de satisfacción realizadas en los módulos.
 * 
 * CAMBIOS V2.0:
 * - Usa n_id_usuario (FK a seg_usuario) en lugar de dni_sece/nombre_sece
 * - Incluye campos de auditoría
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@Schema(description = "Encuesta de satisfacción del usuario registrada en met_encuesta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Encuesta implements Serializable {
    
    private static final long serialVersionUID = 746237126088051314L;
    
    @Schema(description = "ID único de la encuesta", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long nIdEncuesta;
    
    @Schema(description = "ID del módulo donde se realizó la encuesta", example = "1", required = true)
    @JsonAlias({"nIdModulo", "idModulo", "id_modulo", "n_id_modulo", "nidModulo"})
    private Long nIdModulo;              // FK a seg_modulo
    
    @Schema(description = "ID del usuario que respondió la encuesta", example = "1", required = true)
    @JsonAlias({"nIdUsuario", "idUsuario", "id_usuario", "n_id_usuario", "nidUsuario"})
    private Long nIdUsuario;             // FK a seg_usuario
    
    @Schema(description = "Calificación de 1 a 5", example = "5", required = true, minimum = "1", maximum = "5")
    @JsonAlias({"nCalificacion", "calificacion", "n_calificacion", "ncalificacion"})
    private Integer nCalificacion;
    
    @Schema(description = "Fecha y hora de la encuesta", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date fFechaHora;
    
    // Campos de auditoría
    @Schema(description = "Fecha de auditoría", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date fAud;
    
    @Schema(description = "Bandera de auditoría (I/U/D)", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String bAud;
    
    // Campos de compatibilidad (DEPRECATED)
    @Schema(description = "DNI del usuario (DEPRECADO - usar nIdUsuario)", deprecated = true)
    @Deprecated
    private String dniSece;
    
    @Schema(description = "Nombre del usuario (DEPRECADO - usar nIdUsuario)", deprecated = true)
    @Deprecated
    private String nombreSece;
}

package com.ncpp.asistenteexpedientes.asistente.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Bitacora - Tabla met_bitacora (Métricas/Seguimiento)
 * 
 * Registra todas las acciones realizadas en el sistema, vinculada a usuarios.
 * 
 * CAMBIOS PRINCIPALES V2.0:
 * - Ahora usa n_id_usuario (FK a seg_usuario) en lugar de dni_sece/nombre_sece
 * - La tabla se llama met_bitacora (prefijo de métricas)
 * - Incluye campos de auditoría automática (f_aud, b_aud, c_aud_uid)
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bitacora implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;

    private Long nIdBitacora;
    private Long nIdUsuario;          // FK a seg_usuario (reemplaza dni_sece)
    private String cIpModulo;
    private String cCodigoAccion;
    private String tDescripcionAccion;
    private Date fFechaHora;
    
    // Campos de auditoría
    private Date fAud;
    private String bAud;
    private String cAudUid;
    
    // Campos de compatibilidad para migración (DEPRECATED - usar nIdUsuario)
    @Deprecated
    private String dniSece;           // Mantener temporalmente para compatibilidad
    @Deprecated
    private String nombreSece;        // Mantener temporalmente para compatibilidad
    
}

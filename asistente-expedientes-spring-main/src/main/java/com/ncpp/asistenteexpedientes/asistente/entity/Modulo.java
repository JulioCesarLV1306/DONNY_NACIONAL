package com.ncpp.asistenteexpedientes.asistente.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Entidad Modulo - Tabla seg_modulo (Seguridad/Infraestructura)
 * 
 * Representa los módulos físicos (PCs) donde se ejecuta el sistema.
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@Schema(description = "Módulo/PC donde se ejecuta el sistema, registrado en seg_modulo")
@Data
@Entity
@Table(name = "seg_modulo")
public class Modulo implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;

    @Schema(description = "ID único del módulo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_modulo")
    private Long nIdModulo;

    @Schema(description = "Dirección IP del PC", example = "192.168.1.100", required = true, maxLength = 15)
    @Column(name = "c_pc_ip", nullable = false, unique = true, length = 15)
    private String cPcIp;

    @Schema(description = "Usuario del sistema operativo del PC", example = "admin", required = true, maxLength = 300)
    @Column(name = "c_pc_usuario", nullable = false, length = 300)
    private String cPcUsuario;

    @Schema(description = "Clave del usuario del PC (encriptada)", example = "********", required = true, maxLength = 100)
    @Column(name = "c_pc_clave", nullable = false, length = 100)
    private String cPcClave;

    @Schema(description = "Descripción del módulo", example = "Módulo de atención al público", maxLength = 500)
    @Column(name = "x_descripcion", length = 500)
    private String xDescripcion;

    @Schema(description = "Ubicación física del módulo", example = "Primer piso - Ventanilla 1", required = true, maxLength = 200)
    @Column(name = "c_ubicacion", nullable = false, length = 200)
    private String cUbicacion;

    @Schema(description = "Estado del módulo (1=activo, 0=inactivo)", example = "1", required = true, allowableValues = {"0", "1"})
    @Column(name = "n_estado", nullable = false)
    private Integer nEstado;

    // Campos de auditoría
    @Schema(description = "Fecha de auditoría", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "f_aud")
    private Date fAud;

    @Schema(description = "Bandera de auditoría (I/U/D)", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "b_aud", length = 1)
    private String bAud;

    @Schema(description = "UID del usuario que realizó la operación", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "c_aud_uid", length = 30)
    private String cAudUid;
    
}

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Usuario - Tabla seg_usuario (Seguridad)
 * 
 * Nomenclatura de campos refactorizada:
 * - n_ = Número/Secuencia (IDs, contadores)
 * - c_ = Código (DNI, teléfono, identificadores)
 * - x_ = Descripción/Texto (nombres, apellidos, correos)
 * - l_ = Lógico/Indicador (S/N, activo/inactivo)
 * - f_ = Fecha (timestamps, dates)
 * - b_ = Bandera (tipo de auditoría: I/U/D)
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@Schema(description = "Usuario del sistema registrado en seg_usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seg_usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 746237126088051313L;

    @Schema(description = "ID único del usuario (autogenerado)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_usuario")
    private Long nIdUsuario;

    @Schema(description = "Tipo de usuario (1=ciudadano, 2=funcionario, etc.)", example = "1", required = true)
    @Column(name = "n_id_tipo", nullable = false)
    private Integer nIdTipo;

    @Schema(description = "DNI del usuario", example = "12345678", required = true, maxLength = 10)
    @Column(name = "c_dni", nullable = false, unique = true, length = 10)
    private String cDni;

    @Schema(description = "Apellido paterno", example = "Pérez", required = true, maxLength = 100)
    @Column(name = "x_ape_paterno", nullable = false, length = 100)
    private String xApePaterno;

    @Schema(description = "Apellido materno", example = "García", required = true, maxLength = 100)
    @Column(name = "x_ape_materno", nullable = false, length = 100)
    private String xApeMaterno;

    @Schema(description = "Nombres completos", example = "Juan Carlos", required = true, maxLength = 200)
    @Column(name = "x_nombres", nullable = false, length = 200)
    private String xNombres;

    @Schema(description = "Teléfono de contacto", example = "987654321", maxLength = 20)
    @Column(name = "c_telefono", length = 20)
    private String cTelefono;

    @Schema(description = "Correo electrónico", example = "usuario@example.com", maxLength = 150)
    @Column(name = "x_correo", length = 150)
    private String xCorreo;

    @Schema(description = "Indicador de usuario activo (S=activo, N=inactivo)", example = "S", defaultValue = "S", allowableValues = {"S", "N"})
    @Column(name = "l_activo", length = 1)
    private String lActivo = "S";

    // Campos de auditoría (gestionados automáticamente por triggers)
    @Schema(description = "Fecha de auditoría (gestionada por trigger)", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "f_aud")
    private Date fAud;

    @Schema(description = "Bandera de auditoría (I=Insert, U=Update, D=Delete)", accessMode = Schema.AccessMode.READ_ONLY, allowableValues = {"I", "U", "D"})
    @Column(name = "b_aud", length = 1)
    private String bAud;

    @Schema(description = "UID del usuario que realizó la operación (gestionado por trigger)", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "c_aud_uid", length = 30)
    private String cAudUid;

    /**
     * Obtiene el nombre completo del usuario
     * @return Nombre completo (Apellidos + Nombres)
     */
    public String getNombreCompleto() {
        return String.format("%s %s, %s", xApePaterno, xApeMaterno, xNombres);
    }
}

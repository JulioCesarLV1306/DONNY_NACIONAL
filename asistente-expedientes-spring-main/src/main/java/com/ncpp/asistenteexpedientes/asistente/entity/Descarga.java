package com.ncpp.asistenteexpedientes.asistente.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad Descarga - Tabla met_descarga (Control de Descargas)
 * 
 * Controla el estado y progreso de las descargas de archivos.
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Descarga implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;
     
    private Long nIdDescarga;
    private String cKeyDescarga;
    private String xEstado;
    private Integer nPorcentajeDescarga;
    private Integer nConteoDescarga;
    private Integer nTotalDescarga;
    private String xMensajeFinal;
    
    // Campos de auditoría
    private Date fAud;
    private String bAud;
    private String cAudUid;

    // Campos de compatibilidad (DEPRECATED)
    @Deprecated
    private Integer porcentajeCopia;
    @Deprecated
    private Integer conteoCopia;
    @Deprecated
    private Integer totalCopia;

    // Aliases legacy para clientes que aún consumen el contrato anterior.
    public Long getIdDescarga() {
        return nIdDescarga;
    }

    public String getEstado() {
        return xEstado;
    }

    public Integer getPorcentajeDescarga() {
        return nPorcentajeDescarga;
    }

    public Integer getConteoDescarga() {
        return nConteoDescarga;
    }

    public Integer getTotalDescarga() {
        return nTotalDescarga;
    }

    public String getMensajeFinal() {
        return xMensajeFinal;
    }

}

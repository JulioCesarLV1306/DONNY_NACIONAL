package model;

import java.io.Serializable;

/**
 * POJO Descarga - Modelo de seguimiento de descarga/copia de expedientes
 * 
 * Nomenclatura refactorizada V2.0
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class Descarga implements Serializable {

    private static final long serialVersionUID = 746237126088051312L;
     
    private Long nIdDescarga;
    private String cKeyDescarga;
    private String xEstado;
    private Integer nPorcentajeDescarga;
    private Integer nConteoDescarga;
    private Integer nTotalDescarga;
    
    @Deprecated
    private Integer nPorcentajeCopia;
    
    @Deprecated
    private Integer nConteoCopia;
    
    @Deprecated
    private Integer nTotalCopia;
    
    private String xMensajeFinal;

    // Getters y Setters refactorizados
    public Long getNIdDescarga() {
        return nIdDescarga;
    }

    public void setNIdDescarga(Long nIdDescarga) {
        this.nIdDescarga = nIdDescarga;
    }

    public String getCKeyDescarga() {
        return cKeyDescarga;
    }

    public void setCKeyDescarga(String cKeyDescarga) {
        this.cKeyDescarga = cKeyDescarga;
    }

    public String getXEstado() {
        return xEstado;
    }

    public void setXEstado(String xEstado) {
        this.xEstado = xEstado;
    }

    public Integer getNPorcentajeDescarga() {
        return nPorcentajeDescarga;
    }

    public void setNPorcentajeDescarga(Integer nPorcentajeDescarga) {
        this.nPorcentajeDescarga = nPorcentajeDescarga;
    }

    public Integer getNConteoDescarga() {
        return nConteoDescarga;
    }

    public void setNConteoDescarga(Integer nConteoDescarga) {
        this.nConteoDescarga = nConteoDescarga;
    }

    public Integer getNTotalDescarga() {
        return nTotalDescarga;
    }

    public void setNTotalDescarga(Integer nTotalDescarga) {
        this.nTotalDescarga = nTotalDescarga;
    }

    @Deprecated
    public Integer getNPorcentajeCopia() {
        return nPorcentajeCopia;
    }

    @Deprecated
    public void setNPorcentajeCopia(Integer nPorcentajeCopia) {
        this.nPorcentajeCopia = nPorcentajeCopia;
    }

    @Deprecated
    public Integer getNConteoCopia() {
        return nConteoCopia;
    }

    @Deprecated
    public void setNConteoCopia(Integer nConteoCopia) {
        this.nConteoCopia = nConteoCopia;
    }

    @Deprecated
    public Integer getNTotalCopia() {
        return nTotalCopia;
    }

    @Deprecated
    public void setNTotalCopia(Integer nTotalCopia) {
        this.nTotalCopia = nTotalCopia;
    }

    public String getXMensajeFinal() {
        return xMensajeFinal;
    }

    public void setXMensajeFinal(String xMensajeFinal) {
        this.xMensajeFinal = xMensajeFinal;
    }

    // Aliases legacy para el frontend Angular actual.
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

    public Integer getPorcentajeCopia() {
        return nPorcentajeCopia;
    }

    public Integer getConteoCopia() {
        return nConteoCopia;
    }

    public Integer getTotalCopia() {
        return nTotalCopia;
    }

    public String getMensajeFinal() {
        return xMensajeFinal;
    }
}

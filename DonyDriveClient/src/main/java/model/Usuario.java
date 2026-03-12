package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Entity
@Table(name = "seg_usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 746237126088051313L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_usuario")
    private Long nIdUsuario;

    @Column(name = "n_id_tipo", nullable = false)
    private Integer nIdTipo;

    @Column(name = "c_dni", nullable = false, unique = true, length = 10)
    private String cDni;

    @Column(name = "x_ape_paterno", nullable = false, length = 100)
    private String xApePaterno;

    @Column(name = "x_ape_materno", nullable = false, length = 100)
    private String xApeMaterno;

    @Column(name = "x_nombres", nullable = false, length = 200)
    private String xNombres;

    @Column(name = "c_telefono", length = 20)
    private String cTelefono;

    @Column(name = "x_correo", length = 150)
    private String xCorreo;

    @Column(name = "l_activo", length = 1)
    private String lActivo = "S";

    // Campos de auditoría (gestionados automáticamente por triggers)
    @Basic(optional = false)
    @Column(name = "f_aud", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fAud;

    @Column(name = "b_aud", length = 1)
    private String bAud;

    @Column(name = "c_aud_uid", length = 30)
    private String cAudUid;

    // Constructor vacío requerido por JPA
    public Usuario() {
    }

    // Getters y Setters
    public Long getNIdUsuario() {
        return nIdUsuario;
    }

    public void setNIdUsuario(Long nIdUsuario) {
        this.nIdUsuario = nIdUsuario;
    }

    public Integer getNIdTipo() {
        return nIdTipo;
    }

    public void setNIdTipo(Integer nIdTipo) {
        this.nIdTipo = nIdTipo;
    }

    public String getCDni() {
        return cDni;
    }

    public void setCDni(String cDni) {
        this.cDni = cDni;
    }

    public String getXApePaterno() {
        return xApePaterno;
    }

    public void setXApePaterno(String xApePaterno) {
        this.xApePaterno = xApePaterno;
    }

    public String getXApeMaterno() {
        return xApeMaterno;
    }

    public void setXApeMaterno(String xApeMaterno) {
        this.xApeMaterno = xApeMaterno;
    }

    public String getXNombres() {
        return xNombres;
    }

    public void setXNombres(String xNombres) {
        this.xNombres = xNombres;
    }

    public String getCTelefono() {
        return cTelefono;
    }

    public void setCTelefono(String cTelefono) {
        this.cTelefono = cTelefono;
    }

    public String getXCorreo() {
        return xCorreo;
    }

    public void setXCorreo(String xCorreo) {
        this.xCorreo = xCorreo;
    }

    public String getLActivo() {
        return lActivo;
    }

    public void setLActivo(String lActivo) {
        this.lActivo = lActivo;
    }

    public Date getFAud() {
        return fAud;
    }

    public void setFAud(Date fAud) {
        this.fAud = fAud;
    }

    public String getBAud() {
        return bAud;
    }

    public void setBAud(String bAud) {
        this.bAud = bAud;
    }

    public String getCAudUid() {
        return cAudUid;
    }

    public void setCAudUid(String cAudUid) {
        this.cAudUid = cAudUid;
    }

    /**
     * Helper para obtener nombre completo
     */
    public String getNombreCompleto() {
        return xApePaterno + " " + xApeMaterno + " " + xNombres;
    }
}

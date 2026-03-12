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
@Entity
@Table(name = "met_bitacora")
public class Bitacora implements Serializable{

    private static final long serialVersionUID = 746237126088051312L;
    
    public Bitacora (){
        
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_bitacora")
    private Long nIdBitacora;

    @Column(name = "n_id_usuario")
    private Long nIdUsuario;  // FK a seg_usuario

    @Column(name = "c_ip_modulo")
    private String cIpModulo;

    @Column(name = "c_codigo_accion")
    private String cCodigoAccion;

    @Column(name = "t_descripcion_acc")
    private String tDescripcionAccion;
    
    @Basic(optional = false)
    @Column(name = "f_fecha_hora", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fFechaHora;

    // Campos de auditoría
    @Basic(optional = false)
    @Column(name = "f_aud", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fAud;

    @Column(name = "b_aud", length = 1)
    private String bAud;

    @Column(name = "c_aud_uid", length = 30)
    private String cAudUid;

    // Getters y Setters refactorizados
    public Long getNIdBitacora() {
        return nIdBitacora;
    }

    public void setNIdBitacora(Long nIdBitacora) {
        this.nIdBitacora = nIdBitacora;
    }

    public Long getNIdUsuario() {
        return nIdUsuario;
    }

    public void setNIdUsuario(Long nIdUsuario) {
        this.nIdUsuario = nIdUsuario;
    }

    public String getCIpModulo() {
        return cIpModulo;
    }

    public void setCIpModulo(String cIpModulo) {
        this.cIpModulo = cIpModulo;
    }

    public String getCCodigoAccion() {
        return cCodigoAccion;
    }

    public void setCCodigoAccion(String cCodigoAccion) {
        this.cCodigoAccion = cCodigoAccion;
    }

    public String getTDescripcionAccion() {
        return tDescripcionAccion;
    }

    public void setTDescripcionAccion(String tDescripcionAccion) {
        this.tDescripcionAccion = tDescripcionAccion;
    }

    public Date getFFechaHora() {
        return fFechaHora;
    }

    public void setFFechaHora(Date fFechaHora) {
        this.fFechaHora = fFechaHora;
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
}

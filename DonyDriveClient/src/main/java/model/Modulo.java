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
 * Entidad Modulo - Tabla seg_modulo (Infraestructura)
 * 
 * Nomenclatura refactorizada V2.0
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@Entity
@Table(name="seg_modulo")
public class Modulo implements Serializable{
    
    private static final long serialVersionUID = 746237126088051312L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="n_id_modulo")
    private Long nIdModulo;

    @Column(name="c_pc_ip")
    private String cPcIp;

    @Column(name="c_pc_usuario")
    private String cPcUsuario;

    @Column(name="c_pc_clave")
    private String cPcClave;

    @Column(name="x_descripcion")
    private String xDescripcion;

    @Column(name="c_ubicacion")
    private String cUbicacion;

    @Column(name="n_estado")
    private Integer nEstado;

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
    public Long getNIdModulo() {
        return nIdModulo;
    }

    public void setNIdModulo(Long nIdModulo) {
        this.nIdModulo = nIdModulo;
    }

    public String getCPcIp() {
        return cPcIp;
    }

    public void setCPcIp(String cPcIp) {
        this.cPcIp = cPcIp;
    }

    public String getCPcUsuario() {
        return cPcUsuario;
    }

    public void setCPcUsuario(String cPcUsuario) {
        this.cPcUsuario = cPcUsuario;
    }

    public String getCPcClave() {
        return cPcClave;
    }

    public void setCPcClave(String cPcClave) {
        this.cPcClave = cPcClave;
    }

    public String getXDescripcion() {
        return xDescripcion;
    }

    public void setXDescripcion(String xDescripcion) {
        this.xDescripcion = xDescripcion;
    }

    public String getCUbicacion() {
        return cUbicacion;
    }

    public void setCUbicacion(String cUbicacion) {
        this.cUbicacion = cUbicacion;
    }

    public Integer getNEstado() {
        return nEstado;
    }

    public void setNEstado(Integer nEstado) {
        this.nEstado = nEstado;
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


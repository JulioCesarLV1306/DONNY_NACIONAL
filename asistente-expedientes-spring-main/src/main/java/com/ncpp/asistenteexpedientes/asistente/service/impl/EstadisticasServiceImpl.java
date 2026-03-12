package com.ncpp.asistenteexpedientes.asistente.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ncpp.asistenteexpedientes.asistente.database.AccesoAsistente;
import com.ncpp.asistenteexpedientes.asistente.entity.Estadisticas;
import com.ncpp.asistenteexpedientes.asistente.service.EstadisticasService;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

/**
 * Servicio de Estadísticas - Refactorizado V2.0
 * 
 * Tabla refactorizada: met_estadistica
 * Nomenclatura de campos con prefijos técnicos (n_, f_)
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class EstadisticasServiceImpl implements EstadisticasService {

    // Nombres de campos refactorizados (sin prefijo para compatibilidad interna)
    private final String strActas = "n_actas";
    private final String strResoluciones = "n_resoluciones";
    private final String strDocumentos = "n_documentos";
    private final String strHojas = "n_hojas";
    private final String strBytes = "n_bytes";
    private final String strPenal = "n_penal";
    private final String strCivil = "n_civil";
    private final String strFamilia = "n_familia";
    private final String strLaboral = "n_laboral";

    @Override
    public void aumentarActas(long idModulo, int cantidad) {
        aumentarCampo(strActas, idModulo, cantidad);
    }

    @Override
    public void aumentarResoluciones(long idModulo, int cantidad) {
        aumentarCampo(strResoluciones, idModulo, cantidad);
    }

    @Override
    public void aumentarDocumentos(long idModulo, int cantidad) {
        aumentarCampo(strDocumentos, idModulo, cantidad);
    }

    @Override
    public void aumentarVideos(long idModulo, int cantidad) {
        // DEPRECATED: El campo videos no existe en el nuevo esquema
        System.out.println("[EstadisticasServiceImpl] ADVERTENCIA: aumentarVideos() " +
            "es deprecated - campo eliminado en esquema V2.0");
    }

    @Override
    public void aumentarHojas(long idModulo, int cantidad) {
        aumentarCampo(strHojas, idModulo, cantidad);
    }

    @Override
    public void aumentarBytes(long idModulo, long cantidad) {
        aumentarCampo(strBytes, idModulo, cantidad);
    }

    @Override
    public void aumentarPenal(long idModulo, int cantidad) {
        aumentarCampo(strPenal, idModulo, cantidad);
    }

    @Override
    public void aumentarLaboral(long idModulo, int cantidad) {
        aumentarCampo(strLaboral, idModulo, cantidad);
    }

    @Override
    public void aumentarCivil(long idModulo, int cantidad) {
        aumentarCampo(strCivil, idModulo, cantidad);
    }

    @Override
    public void aumentarFamilia(long idModulo, int cantidad) {
        aumentarCampo(strFamilia, idModulo, cantidad);
    }

    private void aumentarCampo(String nombreCampo, long idModulo, long cantidad) {
        Connection cn = null;
        try {
            Estadisticas estadisticas = getActualObject(idModulo);
            
            if (estadisticas != null && estadisticas.getNIdEstadistica() != null && 
                estadisticas.getNIdEstadistica() != 0) {
                
                cn = AccesoAsistente.getConnection();
                cn.setAutoCommit(false);
                
                // SQL refactorizado con nomenclatura met_estadistica
                String sql = "UPDATE met_estadistica SET " + nombreCampo + " = ? " +
                    "WHERE n_id_estadistica = ?";
                
                PreparedStatement pstm = cn.prepareStatement(sql);
                
                // Calcula el nuevo valor según el campo
                switch (nombreCampo) {
                    case "n_actas":
                        pstm.setLong(1, estadisticas.getNActas() + cantidad);
                        break;
                    case "n_resoluciones":
                        pstm.setLong(1, estadisticas.getNResoluciones() + cantidad);
                        break;
                    case "n_documentos":
                        pstm.setLong(1, estadisticas.getNDocumentos() + cantidad);
                        break;
                    case "n_hojas":
                        pstm.setLong(1, estadisticas.getNHojas() + cantidad);
                        break;
                    case "n_bytes":
                        pstm.setLong(1, estadisticas.getNBytes() + cantidad);
                        break;
                    case "n_penal":
                        pstm.setLong(1, estadisticas.getNPenal() + cantidad);
                        break;
                    case "n_civil":
                        pstm.setLong(1, estadisticas.getNCivil() + cantidad);
                        break;
                    case "n_laboral":
                        pstm.setLong(1, estadisticas.getNLaboral() + cantidad);
                        break;
                    case "n_familia":
                        pstm.setLong(1, estadisticas.getNFamilia() + cantidad);
                        break;
                    default:
                        System.err.println("[EstadisticasServiceImpl] Campo desconocido: " + 
                            nombreCampo);
                        pstm.close();
                        return;
                }

                pstm.setLong(2, estadisticas.getNIdEstadistica());
                pstm.executeUpdate();
                cn.commit();
                pstm.close();
                
                System.out.println("[EstadisticasServiceImpl] Actualizado " + nombreCampo + 
                    " en módulo " + idModulo);
            }
        } catch (Exception e) {
            System.err.println("[EstadisticasServiceImpl] ERROR en aumentarCampo: " + 
                e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[EstadisticasServiceImpl] ERROR en rollback: " + 
                    ex.getMessage());
            }
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[EstadisticasServiceImpl] ERROR al cerrar: " + 
                    e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
    }

    private Estadisticas getActualObject(long idModulo) {
        Estadisticas estadisticas = findByDate(idModulo);
        if (estadisticas == null || estadisticas.getNIdEstadistica() == null || 
            estadisticas.getNIdEstadistica() == 0) {
            estadisticas = create(idModulo);
        }
        return estadisticas;
    }

    private Estadisticas findByDate(long idModulo) {
        Connection cn = null;
        Estadisticas estadisticas = new Estadisticas();
        try {
            cn = AccesoAsistente.getConnection();
            
            // SQL refactorizado con nomenclatura met_estadistica
            String sql = "SELECT n_id_estadistica, n_actas, n_resoluciones, n_documentos, " +
                "n_hojas, n_bytes, n_penal, n_laboral, n_familia, n_civil " +
                "FROM met_estadistica WHERE n_id_modulo = ? AND f_fecha = ?";
            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setLong(1, idModulo);
            pstm.setDate(2, getFechaSQL());
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                estadisticas.setNIdEstadistica(rs.getLong("n_id_estadistica"));
                estadisticas.setNActas(rs.getInt("n_actas"));
                estadisticas.setNResoluciones(rs.getInt("n_resoluciones"));
                estadisticas.setNDocumentos(rs.getInt("n_documentos"));
                estadisticas.setNHojas(rs.getInt("n_hojas"));
                estadisticas.setNBytes(rs.getLong("n_bytes"));
                estadisticas.setNPenal(rs.getLong("n_penal"));
                estadisticas.setNLaboral(rs.getLong("n_laboral"));
                estadisticas.setNFamilia(rs.getLong("n_familia"));
                estadisticas.setNCivil(rs.getLong("n_civil"));
                estadisticas.setNIdModulo(idModulo);
            }
            
            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.err.println("[EstadisticasServiceImpl] ERROR en findByDate: " + 
                e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            estadisticas = null;
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[EstadisticasServiceImpl] ERROR al cerrar: " + 
                    e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return estadisticas;
    }

    private Estadisticas create(long idModulo) {
        Connection cn = null;
        Estadisticas estadisticas = new Estadisticas();
        estadisticas.setNActas(0);
        estadisticas.setNDocumentos(0);
        estadisticas.setNResoluciones(0);
        estadisticas.setNHojas(0);
        estadisticas.setNBytes(0L);
        estadisticas.setNPenal(0L);
        estadisticas.setNLaboral(0L);
        estadisticas.setNCivil(0L);
        estadisticas.setNFamilia(0L);
        estadisticas.setNIdModulo(idModulo);
        estadisticas.setFFecha(getFechaSQL());
        
        try {
            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);
            
            // SQL refactorizado con nomenclatura met_estadistica
            String sql = "INSERT INTO met_estadistica(" +
                "n_id_modulo, n_actas, n_resoluciones, n_documentos, n_hojas, f_fecha, " +
                "n_bytes, n_penal, n_laboral, n_civil, n_familia" +
                ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setLong(1, estadisticas.getNIdModulo());
            pstm.setInt(2, estadisticas.getNActas());
            pstm.setInt(3, estadisticas.getNResoluciones());
            pstm.setInt(4, estadisticas.getNDocumentos());
            pstm.setInt(5, estadisticas.getNHojas());
            pstm.setDate(6, (java.sql.Date) estadisticas.getFFecha());
            pstm.setLong(7, estadisticas.getNBytes());
            pstm.setLong(8, estadisticas.getNPenal());
            pstm.setLong(9, estadisticas.getNLaboral());
            pstm.setLong(10, estadisticas.getNCivil());
            pstm.setLong(11, estadisticas.getNFamilia());

            pstm.executeUpdate();
            
            long ls_id = Util.getMaxId("n_id_estadistica", "met_estadistica", cn);
            estadisticas.setNIdEstadistica(ls_id);
            
            cn.commit();
            pstm.close();
            
            System.out.println("[EstadisticasServiceImpl] Estadística creada: " + ls_id);
            
        } catch (Exception e) {
            System.err.println("[EstadisticasServiceImpl] ERROR en create: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            estadisticas = null;
            
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[EstadisticasServiceImpl] ERROR en rollback: " + 
                    ex.getMessage());
            }
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[EstadisticasServiceImpl] ERROR al cerrar: " + 
                    e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return estadisticas;
    }

    private java.sql.Date getFechaSQL() {
        java.util.Date utilDate = new java.util.Date();
        return new java.sql.Date(utilDate.getTime());
    }
}


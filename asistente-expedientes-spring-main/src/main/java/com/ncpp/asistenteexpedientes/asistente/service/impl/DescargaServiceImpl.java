package com.ncpp.asistenteexpedientes.asistente.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ncpp.asistenteexpedientes.asistente.database.AccesoAsistente;
import com.ncpp.asistenteexpedientes.asistente.entity.Descarga;
import com.ncpp.asistenteexpedientes.asistente.service.DescargaService;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

/**
 * Servicio de Descargas - Refactorizado V2.0
 * 
 * Tabla refactorizada: met_descarga
 * Nomenclatura de campos con prefijos técnicos (n_, c_, x_)
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class DescargaServiceImpl implements DescargaService {

    public final static String ESTADO_DESCARGANDO = "descargando";
    public final static String ESTADO_ERROR_DESCARGA = "error-descarga";
    public final static String ESTADO_COMPLETO_DESCARGA = "completo-descarga";
    public final static String ESTADO_FALTA_ESPACIO = "falta-espacio";

    private Descarga create(String keyDescarga) {
        Connection cn = null;
        Descarga descarga = new Descarga();
        try {
            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);
            
            // SQL refactorizado con nomenclatura met_descarga
            String sql = "INSERT INTO met_descarga(" +
                "c_key_descarga, x_estado, n_porcentaje_desc, " +
                "n_conteo_desc, n_total_desc, n_porcentaje_copia, " +
                "n_conteo_copia, n_total_copia, c_aud_uid" +
                ") VALUES(?, 'creado', 0, 0, 0, 0, 0, 0, ?)";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, keyDescarga);
            pstm.setString(2, AccesoAsistente.getAuditUser());
            pstm.executeUpdate();

            // Obtiene el ID generado
            long idDescarga = Util.getMaxId("n_id_descarga", "met_descarga", cn);
            descarga.setNIdDescarga(idDescarga);
            descarga.setCKeyDescarga(keyDescarga);
            
            cn.commit();
            pstm.close();
            
            System.out.println("[DescargaServiceImpl] Descarga creada: " + idDescarga);
            
        } catch (Exception e) {
            System.err.println("[DescargaServiceImpl] ERROR al crear: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            descarga = null;
            
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[DescargaServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[DescargaServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return descarga;
    }

    @Override
    public Descarga createOrUpdateDescarga(String keyDescarga, String estado, 
                                          int archivosDescargados, int totalArchivos) {
        Descarga descarga = find(keyDescarga);
        if (descarga == null || descarga.getNIdDescarga() == null || 
            descarga.getNIdDescarga() == 0) {
            descarga = create(keyDescarga);
        } else {
            descarga = updateDescarga(descarga, estado, archivosDescargados, totalArchivos);
        }
        return descarga;
    }

    @Override
    public Descarga find(String keyDescarga) {
        Connection cn = null;
        Descarga descarga = new Descarga();
        try {
            cn = AccesoAsistente.getConnection();
            
            // SQL refactorizado con nomenclatura met_descarga
            String sql = "SELECT n_id_descarga, c_key_descarga, x_estado, " +
                "n_porcentaje_desc, n_conteo_desc, n_total_desc, " +
                "n_porcentaje_copia, n_conteo_copia, n_total_copia, x_mensaje_final " +
                "FROM met_descarga WHERE c_key_descarga = ?";
            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, keyDescarga);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                descarga.setNIdDescarga(rs.getLong("n_id_descarga"));
                descarga.setCKeyDescarga(rs.getString("c_key_descarga"));
                descarga.setXEstado(rs.getString("x_estado"));
                descarga.setNPorcentajeDescarga(rs.getInt("n_porcentaje_desc"));
                descarga.setNConteoDescarga(rs.getInt("n_conteo_desc"));
                descarga.setNTotalDescarga(rs.getInt("n_total_desc"));
                descarga.setNPorcentajeCopia(rs.getInt("n_porcentaje_copia"));
                descarga.setNConteoCopia(rs.getInt("n_conteo_copia"));
                descarga.setNTotalCopia(rs.getInt("n_total_copia"));
                descarga.setXMensajeFinal(rs.getString("x_mensaje_final"));
            }
            
            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.err.println("[DescargaServiceImpl] ERROR en find: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            descarga = null;
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[DescargaServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return descarga;
    }

    @Override
    public Descarga addMensajeFinal(String keyDescarga, String mensajeFinal) {
        // TODO: Implementar actualización de mensaje final
        return null;
    }

    private Descarga updateDescarga(Descarga descarga, String estado, 
                                   int conteoDescarga, int totalDescarga) {
        Connection cn = null;
        try {
            if (descarga != null && descarga.getNIdDescarga() != null && 
                descarga.getNIdDescarga() != 0) {
                
                cn = AccesoAsistente.getConnection();
                cn.setAutoCommit(false);
                
                // SQL refactorizado con nomenclatura met_descarga
                String sql = "UPDATE met_descarga SET " +
                    "x_estado = ?, n_porcentaje_desc = ?, " +
                    "n_conteo_desc = ?, n_total_desc = ?, c_aud_uid = ? " +
                    "WHERE n_id_descarga = ?";
                
                PreparedStatement pstm = cn.prepareStatement(sql);
                pstm.setString(1, estado);
                pstm.setInt(2, Util.getPorcentajeDescarga(conteoDescarga, totalDescarga));
                pstm.setInt(3, conteoDescarga);
                pstm.setInt(4, totalDescarga);
                pstm.setString(5, AccesoAsistente.getAuditUser());
                pstm.setLong(6, descarga.getNIdDescarga());
                
                pstm.executeUpdate();
                cn.commit();
                pstm.close();

                // Actualiza el objeto en memoria
                descarga.setXEstado(estado);
                descarga.setNPorcentajeDescarga(
                    Util.getPorcentajeDescarga(conteoDescarga, totalDescarga));
                descarga.setNConteoDescarga(conteoDescarga);
                descarga.setNTotalDescarga(totalDescarga);
                if (descarga.getNPorcentajeCopia() == null) {
                    descarga.setNPorcentajeCopia(0);
                }
                if (descarga.getNConteoCopia() == null) {
                    descarga.setNConteoCopia(0);
                }
                if (descarga.getNTotalCopia() == null) {
                    descarga.setNTotalCopia(0);
                }
                
                System.out.println("[DescargaServiceImpl] Descarga actualizada: " + 
                    descarga.getNIdDescarga());
            }
        } catch (Exception e) {
            System.err.println("[DescargaServiceImpl] ERROR en update: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[DescargaServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[DescargaServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return descarga;
    }
}


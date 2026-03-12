package service;

import config.AccesoAsistente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Descarga;
import util.Util;

/**
 * Servicio para gestión de descargas/copias de expedientes
 * 
 * Refactorizado V2.0 para nueva nomenclatura y esquema met_descarga
 * 
 * @author JC
 * @version 2.0
 */
public class DescargaService {

    public final static String ESTADO_COPIANDO = "copiando";
    public final static String ESTADO_ERROR_COPIA = "error-copia";
    public final static String ESTADO_COMPLETO_COPIA = "completo-copia";

    public Descarga find(String keyDescarga) {
        Connection cn = null;
        Descarga descarga = new Descarga();
        try {
            cn = AccesoAsistente.getConnection();
            String sql = "SELECT n_id_descarga, c_key_descarga, x_estado, n_porcentaje_desc, "
                    + "n_conteo_desc, n_total_desc, n_porcentaje_copia, n_conteo_copia, "
                    + "n_total_copia, x_mensaje_final FROM met_descarga WHERE c_key_descarga = ?";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, keyDescarga);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                mapRow(rs, descarga);
            }
            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            descarga = null;
        } finally {
            try {
                cn.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return descarga;
    }

    public Descarga findByContext(String ipModulo, String dniPersona, String nUnico,
                                  int nIncidente, String keyEleccion, String[] fechas) {
        Connection cn = null;
        Descarga descarga = new Descarga();
        try {
            cn = AccesoAsistente.getConnection();
            String sql = "SELECT n_id_descarga, c_key_descarga, x_estado, n_porcentaje_desc, "
                    + "n_conteo_desc, n_total_desc, n_porcentaje_copia, n_conteo_copia, "
                    + "n_total_copia, x_mensaje_final "
                    + "FROM met_descarga "
                    + "WHERE c_key_descarga LIKE ? "
                    + "ORDER BY n_id_descarga DESC LIMIT 1";
            String keyPattern = "%/" + ipModulo + "/" + dniPersona + "/" + nUnico + "/"
                    + nIncidente + "/" + keyEleccion.toUpperCase() + "/"
                    + concatenarFechas(fechas);

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, keyPattern);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                mapRow(rs, descarga);
            }
            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            descarga = null;
        } finally {
            try {
                cn.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return descarga;
    }

    public Descarga updateCopia(Descarga descarga, String estado, int conteoCopia, int totalCopia) {
        Connection cn = null;
        try {
            if (descarga != null && descarga.getNIdDescarga() != null && descarga.getNIdDescarga() != 0) {
                cn = AccesoAsistente.getConnection();
                cn.setAutoCommit(false);
                String sql = "UPDATE met_descarga SET x_estado = ?, n_porcentaje_copia = ?, "
                        + "n_conteo_copia = ?, n_total_copia = ? WHERE n_id_descarga = ?";
                PreparedStatement pstm = cn.prepareStatement(sql);
                pstm.setString(1, estado);
                pstm.setInt(2, Util.getPorcentajeCopia(conteoCopia, totalCopia));
                pstm.setInt(3, conteoCopia);
                pstm.setInt(4, totalCopia);
                pstm.setLong(5, descarga.getNIdDescarga());
                pstm.executeUpdate();
                cn.commit();
                pstm.close();

                descarga.setXEstado(estado);
                descarga.setNPorcentajeCopia(Util.getPorcentajeCopia(conteoCopia, totalCopia));
                descarga.setNConteoCopia(conteoCopia);
                descarga.setNTotalCopia(totalCopia);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                cn.close();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return descarga;
    }

    private void mapRow(ResultSet rs, Descarga descarga) throws Exception {
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

    private String concatenarFechas(String[] fechasElegidas) {
        StringBuilder fechas = new StringBuilder();
        for (String fechaElegida : fechasElegidas) {
            fechas.append(fechaElegida.replace("-", ""));
        }
        return fechas.toString();
    }

}

package com.ncpp.asistenteexpedientes.asistente.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.database.AccesoAsistente;
import com.ncpp.asistenteexpedientes.asistente.entity.Modulo;
import com.ncpp.asistenteexpedientes.asistente.service.ModuloService;
import com.ncpp.asistenteexpedientes.util.LogDony;

/**
 * Implementación CRUD para la tabla seg_modulo.
 */
public class ModuloServiceImpl implements ModuloService {

    @Override
    public List<Modulo> findAll() {
        Connection cn = null;
        List<Modulo> modulos = new ArrayList<>();
        try {
            cn = AccesoAsistente.getConnection();

            String sql = "SELECT n_id_modulo, c_pc_ip, c_pc_usuario, c_pc_clave, x_descripcion, " +
                "c_ubicacion, n_estado, f_aud, b_aud, c_aud_uid " +
                "FROM seg_modulo ORDER BY n_id_modulo DESC";

            PreparedStatement pstm = cn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                modulos.add(mapModulo(rs));
            }

            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.err.println("[ModuloServiceImpl] ERROR en findAll: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[ModuloServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return modulos;
    }

    @Override
    public Modulo findById(Long nIdModulo) {
        Connection cn = null;
        Modulo modulo = null;
        try {
            cn = AccesoAsistente.getConnection();

            String sql = "SELECT n_id_modulo, c_pc_ip, c_pc_usuario, c_pc_clave, x_descripcion, " +
                "c_ubicacion, n_estado, f_aud, b_aud, c_aud_uid " +
                "FROM seg_modulo WHERE n_id_modulo = ?";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setLong(1, nIdModulo);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                modulo = mapModulo(rs);
            }

            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.err.println("[ModuloServiceImpl] ERROR al buscar por ID: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[ModuloServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return modulo;
    }

    @Override
    public Modulo findByIp(String cPcIp) {
        Connection cn = null;
        Modulo modulo = null;
        try {
            cn = AccesoAsistente.getConnection();

            String sql = "SELECT n_id_modulo, c_pc_ip, c_pc_usuario, c_pc_clave, x_descripcion, " +
                "c_ubicacion, n_estado, f_aud, b_aud, c_aud_uid " +
                "FROM seg_modulo WHERE c_pc_ip = ?";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cPcIp);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                modulo = mapModulo(rs);
            }

            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.err.println("[ModuloServiceImpl] ERROR al buscar por IP: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[ModuloServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return modulo;
    }

    @Override
    public Modulo create(Modulo modulo) {
        Connection cn = null;
        try {
            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);

            String auditUser = modulo.getCPcUsuario() != null ? modulo.getCPcUsuario() : AccesoAsistente.getAuditUser();
            AccesoAsistente.setAuditUser(auditUser);

            String sql = "INSERT INTO seg_modulo(" +
                "c_pc_ip, c_pc_usuario, c_pc_clave, x_descripcion, c_ubicacion, n_estado, c_aud_uid" +
                ") VALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstm = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, modulo.getCPcIp());
            pstm.setString(2, modulo.getCPcUsuario());
            pstm.setString(3, modulo.getCPcClave());
            pstm.setString(4, modulo.getXDescripcion());
            pstm.setString(5, modulo.getCUbicacion());
            pstm.setInt(6, modulo.getNEstado() != null ? modulo.getNEstado() : 1);
            pstm.setString(7, auditUser);

            pstm.executeUpdate();

            ResultSet rs = pstm.getGeneratedKeys();
            if (rs.next()) {
                modulo.setNIdModulo(rs.getLong(1));
            }

            cn.commit();
            pstm.close();

            return findById(modulo.getNIdModulo());
        } catch (Exception e) {
            System.err.println("[ModuloServiceImpl] ERROR al crear: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);

            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[ModuloServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            AccesoAsistente.clearAuditUser();
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[ModuloServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return modulo;
    }

    @Override
    public Modulo update(Long nIdModulo, Modulo modulo) {
        Connection cn = null;
        try {
            Modulo actual = findById(nIdModulo);
            if (actual == null) {
                return null;
            }

            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);

            String auditUser = modulo.getCPcUsuario() != null ? modulo.getCPcUsuario() : actual.getCPcUsuario();
            AccesoAsistente.setAuditUser(auditUser);

            String sql = "UPDATE seg_modulo SET " +
                "c_pc_ip = ?, c_pc_usuario = ?, c_pc_clave = ?, x_descripcion = ?, " +
                "c_ubicacion = ?, n_estado = ?, c_aud_uid = ? " +
                "WHERE n_id_modulo = ?";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, modulo.getCPcIp() != null ? modulo.getCPcIp() : actual.getCPcIp());
            pstm.setString(2, modulo.getCPcUsuario() != null ? modulo.getCPcUsuario() : actual.getCPcUsuario());
            pstm.setString(3, modulo.getCPcClave() != null ? modulo.getCPcClave() : actual.getCPcClave());
            pstm.setString(4, modulo.getXDescripcion() != null ? modulo.getXDescripcion() : actual.getXDescripcion());
            pstm.setString(5, modulo.getCUbicacion() != null ? modulo.getCUbicacion() : actual.getCUbicacion());
            pstm.setInt(6, modulo.getNEstado() != null ? modulo.getNEstado() : actual.getNEstado());
            pstm.setString(7, auditUser);
            pstm.setLong(8, nIdModulo);

            int updatedRows = pstm.executeUpdate();
            cn.commit();
            pstm.close();

            if (updatedRows == 0) {
                return null;
            }

            return findById(nIdModulo);
        } catch (Exception e) {
            System.err.println("[ModuloServiceImpl] ERROR al actualizar: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);

            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[ModuloServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            AccesoAsistente.clearAuditUser();
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[ModuloServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Long nIdModulo) {
        Connection cn = null;
        try {
            Modulo actual = findById(nIdModulo);
            if (actual == null) {
                return false;
            }

            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);

            String auditUser = actual.getCPcUsuario() != null ? actual.getCPcUsuario() : AccesoAsistente.getAuditUser();
            AccesoAsistente.setAuditUser(auditUser);

            String sql = "DELETE FROM seg_modulo WHERE n_id_modulo = ?";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setLong(1, nIdModulo);

            int deletedRows = pstm.executeUpdate();
            cn.commit();
            pstm.close();

            return deletedRows > 0;
        } catch (Exception e) {
            System.err.println("[ModuloServiceImpl] ERROR al eliminar: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);

            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[ModuloServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            AccesoAsistente.clearAuditUser();
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[ModuloServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return false;
    }

    private Modulo mapModulo(ResultSet rs) throws Exception {
        Modulo modulo = new Modulo();
        modulo.setNIdModulo(rs.getLong("n_id_modulo"));
        modulo.setCPcIp(rs.getString("c_pc_ip"));
        modulo.setCPcUsuario(rs.getString("c_pc_usuario"));
        modulo.setCPcClave(rs.getString("c_pc_clave"));
        modulo.setXDescripcion(rs.getString("x_descripcion"));
        modulo.setCUbicacion(rs.getString("c_ubicacion"));
        modulo.setNEstado(rs.getInt("n_estado"));
        modulo.setFAud(rs.getTimestamp("f_aud"));
        modulo.setBAud(rs.getString("b_aud"));
        modulo.setCAudUid(rs.getString("c_aud_uid"));
        return modulo;
    }
}

package com.ncpp.asistenteexpedientes.asistente.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.ncpp.asistenteexpedientes.asistente.database.AccesoAsistente;
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.UsuarioService;
import com.ncpp.asistenteexpedientes.util.LogDony;

/**
 * Implementación del Servicio de Usuarios - V2.0
 * 
 * Gestiona operaciones sobre seg_usuario con nomenclatura refactorizada.
 * 
 * FUNCIONALIDADES:
 * - Búsqueda de usuarios por DNI
 * - Creación automática de usuarios con parsing de nombre completo
 * - Validación de duplicados
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class UsuarioServiceImpl implements UsuarioService {

    @Override
    public List<Usuario> findAll() {
        Connection cn = null;
        List<Usuario> usuarios = new ArrayList<>();
        try {
            cn = AccesoAsistente.getConnection();

            String sql = "SELECT n_id_usuario, n_id_tipo, c_dni, x_ape_paterno, " +
                "x_ape_materno, x_nombres, c_telefono, x_correo, l_activo, " +
                "f_aud, b_aud, c_aud_uid " +
                "FROM seg_usuario ORDER BY n_id_usuario DESC";

            PreparedStatement pstm = cn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                usuarios.add(mapUsuario(rs));
            }

            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.err.println("[UsuarioServiceImpl] ERROR en findAll: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[UsuarioServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return usuarios;
    }

    @Override
    public Usuario findById(Long nIdUsuario) {
        Connection cn = null;
        Usuario usuario = null;
        try {
            cn = AccesoAsistente.getConnection();

            String sql = "SELECT n_id_usuario, n_id_tipo, c_dni, x_ape_paterno, " +
                "x_ape_materno, x_nombres, c_telefono, x_correo, l_activo, " +
                "f_aud, b_aud, c_aud_uid " +
                "FROM seg_usuario WHERE n_id_usuario = ?";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setLong(1, nIdUsuario);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                usuario = mapUsuario(rs);
            }

            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.err.println("[UsuarioServiceImpl] ERROR al buscar por ID: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[UsuarioServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return usuario;
    }

    @Override
    public Usuario findByDni(String dni) {
        Connection cn = null;
        Usuario usuario = null;
        try {
            cn = AccesoAsistente.getConnection();
            
            String sql = "SELECT n_id_usuario, n_id_tipo, c_dni, x_ape_paterno, " +
                "x_ape_materno, x_nombres, c_telefono, x_correo, l_activo, " +
                "f_aud, b_aud, c_aud_uid " +
                "FROM seg_usuario WHERE c_dni = ?";
            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, dni);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                usuario = mapUsuario(rs);
            }
            
            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.err.println("[UsuarioServiceImpl] ERROR al buscar por DNI: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[UsuarioServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return usuario;
    }

    @Override
    public Usuario authenticateByCorreoAndDni(String xCorreo, String cDni) {
        Connection cn = null;
        Usuario usuario = null;
        try {
            cn = AccesoAsistente.getConnection();

            String sql = "SELECT n_id_usuario, n_id_tipo, c_dni, x_ape_paterno, " +
                "x_ape_materno, x_nombres, c_telefono, x_correo, l_activo, " +
                "f_aud, b_aud, c_aud_uid " +
                "FROM seg_usuario " +
                "WHERE LOWER(x_correo) = LOWER(?) AND c_dni = ? AND l_activo = 'S'";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, xCorreo);
            pstm.setString(2, cDni);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                usuario = mapUsuario(rs);
            }

            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.err.println("[UsuarioServiceImpl] ERROR en authenticateByCorreoAndDni: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[UsuarioServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return usuario;
    }

    @Override
    public Usuario createIfNotExists(String dni, String nombreCompleto) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("DNI es obligatorio para crear o buscar usuario.");
        }

        String dniNormalizado = dni.trim();
        String nombreNormalizado = (nombreCompleto == null || nombreCompleto.trim().isEmpty())
            ? "USUARIO " + dniNormalizado
            : nombreCompleto.trim();

        // Primero intenta buscar el usuario
        Usuario usuario = findByDni(dniNormalizado);
        
        if (usuario != null) {
            System.out.println("[UsuarioServiceImpl] Usuario ya existe: " + dniNormalizado);
            return usuario;
        }
        
        // Si no existe, lo crea
        usuario = new Usuario();
        usuario.setCDni(dniNormalizado);
        usuario.setNIdTipo(9); // Tipo "Invitado" por defecto (n_id_tipo = 9)
        usuario.setLActivo("S");
        
        // Parsear nombre completo en formato "APELLIDO_PAT APELLIDO_MAT NOMBRES"
        String[] partes = nombreNormalizado.split("\\s+");
        if (partes.length >= 3) {
            usuario.setXApePaterno(partes[0]);
            usuario.setXApeMaterno(partes[1]);
            // Los demás son los nombres
            StringBuilder nombres = new StringBuilder();
            for (int i = 2; i < partes.length; i++) {
                if (i > 2) nombres.append(" ");
                nombres.append(partes[i]);
            }
            usuario.setXNombres(nombres.toString());
        } else if (partes.length == 2) {
            usuario.setXApePaterno(partes[0]);
            usuario.setXApeMaterno("");
            usuario.setXNombres(partes[1]);
        } else if (partes.length == 1) {
            usuario.setXApePaterno(partes[0]);
            usuario.setXApeMaterno("");
            usuario.setXNombres("");
        }
        
        return create(usuario);
    }

    @Override
    public Usuario create(Usuario usuario) {
        Connection cn = null;
        try {
            if (usuario == null) {
                return null;
            }

            String dni = usuario.getCDni();
            if (dni != null && !dni.trim().isEmpty()) {
                Usuario existente = findByDni(dni);
                if (existente != null) {
                    return update(existente.getNIdUsuario(), usuario);
                }
            }

            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);
            
            // Establece el usuario de auditoría
            String auditUser = usuario.getCDni();
            AccesoAsistente.setAuditUser(auditUser);
            
            String sql = "INSERT INTO seg_usuario(" +
                "n_id_tipo, c_dni, x_ape_paterno, x_ape_materno, x_nombres, " +
                "c_telefono, x_correo, l_activo, c_aud_uid" +
                ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstm = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstm.setInt(1, usuario.getNIdTipo() != null ? usuario.getNIdTipo() : 9);
            pstm.setString(2, dni);
            pstm.setString(3, usuario.getXApePaterno());
            pstm.setString(4, usuario.getXApeMaterno());
            pstm.setString(5, usuario.getXNombres());
            pstm.setString(6, usuario.getCTelefono());
            pstm.setString(7, usuario.getXCorreo());
            pstm.setString(8, usuario.getLActivo() != null ? usuario.getLActivo() : "S");
            pstm.setString(9, auditUser);
            
            pstm.executeUpdate();
            
            // Obtener el ID generado
            ResultSet rs = pstm.getGeneratedKeys();
            if (rs.next()) {
                usuario.setNIdUsuario(rs.getLong(1));
            }
            
            cn.commit();
            pstm.close();

            return findById(usuario.getNIdUsuario());
            
        } catch (Exception e) {
            System.err.println("[UsuarioServiceImpl] ERROR al crear: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[UsuarioServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            AccesoAsistente.clearAuditUser();
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[UsuarioServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return usuario;
    }

    @Override
    public Usuario update(Long nIdUsuario, Usuario usuario) {
        Connection cn = null;
        try {
            Usuario actual = findById(nIdUsuario);
            if (actual == null) {
                return null;
            }

            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);

            String auditUser = usuario.getCDni();
            if (auditUser == null || auditUser.trim().isEmpty()) {
                auditUser = actual.getCDni();
            }
            AccesoAsistente.setAuditUser(auditUser);

            String sql = "UPDATE seg_usuario SET " +
                "n_id_tipo = ?, c_dni = ?, x_ape_paterno = ?, x_ape_materno = ?, " +
                "x_nombres = ?, c_telefono = ?, x_correo = ?, l_activo = ?, c_aud_uid = ? " +
                "WHERE n_id_usuario = ?";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setInt(1, usuario.getNIdTipo() != null ? usuario.getNIdTipo() : actual.getNIdTipo());
            pstm.setString(2, usuario.getCDni() != null ? usuario.getCDni() : actual.getCDni());
            pstm.setString(3, usuario.getXApePaterno() != null ? usuario.getXApePaterno() : actual.getXApePaterno());
            pstm.setString(4, usuario.getXApeMaterno() != null ? usuario.getXApeMaterno() : actual.getXApeMaterno());
            pstm.setString(5, usuario.getXNombres() != null ? usuario.getXNombres() : actual.getXNombres());
            pstm.setString(6, usuario.getCTelefono() != null ? usuario.getCTelefono() : actual.getCTelefono());
            pstm.setString(7, usuario.getXCorreo() != null ? usuario.getXCorreo() : actual.getXCorreo());
            pstm.setString(8, usuario.getLActivo() != null ? usuario.getLActivo() : actual.getLActivo());
            pstm.setString(9, auditUser);
            pstm.setLong(10, nIdUsuario);

            int updatedRows = pstm.executeUpdate();
            cn.commit();
            pstm.close();

            if (updatedRows == 0) {
                return null;
            }

            return findById(nIdUsuario);

        } catch (Exception e) {
            System.err.println("[UsuarioServiceImpl] ERROR al actualizar: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);

            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[UsuarioServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            AccesoAsistente.clearAuditUser();
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[UsuarioServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return null;
    }

    @Override
    public boolean delete(Long nIdUsuario) {
        Connection cn = null;
        try {
            Usuario actual = findById(nIdUsuario);
            if (actual == null) {
                return false;
            }

            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);

            String auditUser = actual.getCDni() != null ? actual.getCDni() : AccesoAsistente.getAuditUser();
            AccesoAsistente.setAuditUser(auditUser);

            String sql = "DELETE FROM seg_usuario WHERE n_id_usuario = ?";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setLong(1, nIdUsuario);

            int deletedRows = pstm.executeUpdate();
            cn.commit();
            pstm.close();

            return deletedRows > 0;

        } catch (Exception e) {
            System.err.println("[UsuarioServiceImpl] ERROR al eliminar: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);

            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[UsuarioServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            AccesoAsistente.clearAuditUser();
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[UsuarioServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return false;
    }

    private Usuario mapUsuario(ResultSet rs) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNIdUsuario(rs.getLong("n_id_usuario"));
        usuario.setNIdTipo(rs.getInt("n_id_tipo"));
        usuario.setCDni(rs.getString("c_dni"));
        usuario.setXApePaterno(rs.getString("x_ape_paterno"));
        usuario.setXApeMaterno(rs.getString("x_ape_materno"));
        usuario.setXNombres(rs.getString("x_nombres"));
        usuario.setCTelefono(rs.getString("c_telefono"));
        usuario.setXCorreo(rs.getString("x_correo"));
        usuario.setLActivo(rs.getString("l_activo"));
        usuario.setFAud(rs.getTimestamp("f_aud"));
        usuario.setBAud(rs.getString("b_aud"));
        usuario.setCAudUid(rs.getString("c_aud_uid"));
        return usuario;
    }
}

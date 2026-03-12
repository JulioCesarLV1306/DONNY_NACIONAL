package com.ncpp.asistenteexpedientes.asistente.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
public class UsuarioServiceImpl implements UsuarioService {

    @Override
    public Usuario findByDni(String dni) {
        Connection cn = null;
        Usuario usuario = null;
        try {
            cn = AccesoAsistente.getConnection();
            
            String sql = "SELECT n_id_usuario, n_id_tipo, c_dni, x_ape_paterno, " +
                "x_ape_materno, x_nombres, c_telefono, x_correo, l_activo " +
                "FROM seg_usuario WHERE c_dni = ?";
            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, dni);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setNIdUsuario(rs.getLong("n_id_usuario"));
                usuario.setNIdTipo(rs.getInt("n_id_tipo"));
                usuario.setCDni(rs.getString("c_dni"));
                usuario.setXApePaterno(rs.getString("x_ape_paterno"));
                usuario.setXApeMaterno(rs.getString("x_ape_materno"));
                usuario.setXNombres(rs.getString("x_nombres"));
                usuario.setCTelefono(rs.getString("c_telefono"));
                usuario.setXCorreo(rs.getString("x_correo"));
                usuario.setLActivo(rs.getString("l_activo"));
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
    public Usuario createIfNotExists(String dni, String nombreCompleto) {
        // Primero intenta buscar el usuario
        Usuario usuario = findByDni(dni);
        
        if (usuario != null) {
            System.out.println("[UsuarioServiceImpl] Usuario ya existe: " + dni);
            return usuario;
        }
        
        // Si no existe, lo crea
        usuario = new Usuario();
        usuario.setCDni(dni);
        usuario.setNIdTipo(9); // Tipo "Invitado" por defecto (n_id_tipo = 9)
        usuario.setLActivo("S");
        
        // Parsear nombre completo en formato "APELLIDO_PAT APELLIDO_MAT NOMBRES"
        String[] partes = nombreCompleto.trim().split("\\s+");
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
            pstm.setString(2, usuario.getCDni());
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
            
            System.out.println("[UsuarioServiceImpl] Usuario creado con ID: " + 
                usuario.getNIdUsuario());
            
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
}

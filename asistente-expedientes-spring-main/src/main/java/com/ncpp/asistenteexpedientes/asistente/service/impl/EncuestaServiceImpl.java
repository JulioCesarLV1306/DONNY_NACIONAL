package com.ncpp.asistenteexpedientes.asistente.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ncpp.asistenteexpedientes.asistente.database.AccesoAsistente;
import com.ncpp.asistenteexpedientes.asistente.entity.Encuesta;
import com.ncpp.asistenteexpedientes.asistente.service.EncuestaService;
import com.ncpp.asistenteexpedientes.util.LogDony;

/**
 * Servicio de Encuestas - Refactorizado V2.0
 * 
 * Tabla refactorizada: met_encuesta
 * Ahora vinculada a seg_usuario mediante n_id_usuario
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class EncuestaServiceImpl implements EncuestaService {

    @Override
    public void create(Encuesta encuesta) {
        Connection cn = null;
        try {
            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);
            
            // Validar que vengan los campos obligatorios
            if (encuesta.getNIdUsuario() == null) {
                throw new IllegalArgumentException(
                    "n_id_usuario es obligatorio. Use UsuarioService.createIfNotExists() " +
                    "para obtener/crear el usuario antes de insertar la encuesta."
                );
            }
            if (encuesta.getNIdModulo() == null) {
                throw new IllegalArgumentException("n_id_modulo es obligatorio.");
            }
            if (encuesta.getNCalificacion() == null) {
                throw new IllegalArgumentException("n_calificacion es obligatorio.");
            }
            if (encuesta.getNCalificacion() < 1 || encuesta.getNCalificacion() > 5) {
                throw new IllegalArgumentException("n_calificacion debe estar entre 1 y 5.");
            }
            
            // SQL refactorizado con nomenclatura met_encuesta
            String sql = "INSERT INTO met_encuesta(" +
                "n_id_modulo, n_id_usuario, n_calificacion" +
                ") VALUES(?, ?, ?)";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setLong(1, encuesta.getNIdModulo());
            pstm.setLong(2, encuesta.getNIdUsuario());
            pstm.setInt(3, encuesta.getNCalificacion());
            
            pstm.executeUpdate();
            cn.commit();
            pstm.close();
            
            System.out.println("[EncuestaServiceImpl] Encuesta registrada para usuario ID: " + 
                encuesta.getNIdUsuario() + ", calificación: " + encuesta.getNCalificacion());
        } catch (IllegalArgumentException e) {
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[EncuestaServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
            throw e;
        } catch (Exception e) {
            System.err.println("[EncuestaServiceImpl] ERROR al crear: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[EncuestaServiceImpl] ERROR en rollback: " + ex.getMessage());
            }

            throw new RuntimeException("Error al crear encuesta", e);
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[EncuestaServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
    }

    @Override
    public Encuesta findByDni(String dni) {
        Connection cn = null;
        Encuesta encuesta = null;
        try {
            cn = AccesoAsistente.getConnection();
            
            // SQL refactorizado con join a seg_usuario
            // Busca por c_dni en lugar de dni_sece
            String sql = "SELECT e.n_id_encuesta, e.n_id_modulo, e.n_id_usuario, " +
                "u.c_dni, u.x_ape_paterno, u.x_ape_materno, u.x_nombres, " +
                "e.n_calificacion, e.f_fecha_hora " +
                "FROM met_encuesta e " +
                "INNER JOIN seg_usuario u ON e.n_id_usuario = u.n_id_usuario " +
                "WHERE u.c_dni = ?";
            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, dni);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                encuesta = new Encuesta();
                encuesta.setNIdEncuesta(rs.getLong("n_id_encuesta"));
                encuesta.setNIdModulo(rs.getLong("n_id_modulo"));
                encuesta.setNIdUsuario(rs.getLong("n_id_usuario"));
                
                // Compatibilidad temporal: mantiene dni y nombre completo
                encuesta.setDniSece(rs.getString("c_dni"));
                String nombreCompleto = String.format("%s %s, %s", 
                    rs.getString("x_ape_paterno"),
                    rs.getString("x_ape_materno"),
                    rs.getString("x_nombres"));
                encuesta.setNombreSece(nombreCompleto);
                
                encuesta.setNCalificacion(rs.getInt("n_calificacion"));
                encuesta.setFFechaHora(rs.getTimestamp("f_fecha_hora"));
            }
            
            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.err.println("[EncuestaServiceImpl] ERROR en findByDni: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            encuesta = null;
        } finally {
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[EncuestaServiceImpl] ERROR al cerrar: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return encuesta;
    }
}


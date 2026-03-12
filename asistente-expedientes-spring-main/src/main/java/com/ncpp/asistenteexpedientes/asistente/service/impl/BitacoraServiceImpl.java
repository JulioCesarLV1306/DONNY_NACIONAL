package com.ncpp.asistenteexpedientes.asistente.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ncpp.asistenteexpedientes.asistente.database.AccesoAsistente;
import com.ncpp.asistenteexpedientes.asistente.entity.Bitacora;
import com.ncpp.asistenteexpedientes.asistente.service.BitacoraService;
import com.ncpp.asistenteexpedientes.util.LogDony;

/**
 * Servicio de Bitácora - Refactorizado V2.0
 * 
 * CAMBIOS PRINCIPALES:
 * - Usa tabla met_bitacora con nomenclatura refactorizada
 * - Integra manejo de c_aud_uid para auditoría automática
 * - Preserva compatibilidad con dni_sece mediante mapeo interno
 * 
 * NOTA: Los campos dni_sece y nombre_sece se mantienen en el DTO para 
 * compatibilidad, pero internamente se deben mapear a n_id_usuario.
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class BitacoraServiceImpl implements BitacoraService {

    @Override
    public void create(Bitacora bitacora) {  
        Connection cn = null;
        try {
            cn = AccesoAsistente.getConnection();
            cn.setAutoCommit(false);
            
            // Validar que venga n_id_usuario (campo obligatorio en nuevo esquema)
            if (bitacora.getNIdUsuario() == null) {
                throw new IllegalArgumentException(
                    "n_id_usuario es obligatorio. Use UsuarioService.createIfNotExists() " +
                    "para obtener/crear el usuario antes de insertar la bitácora."
                );
            }
            
            // Establece el usuario de auditoría para el contexto actual
            String ls_audit_user = bitacora.getDniSece() != null ? 
                bitacora.getDniSece() : AccesoAsistente.getAuditUser();
            if (ls_audit_user == null) {
                ls_audit_user = "SYSTEM";
            }
            AccesoAsistente.setAuditUser(ls_audit_user);
            
            // SQL refactorizado con nomenclatura met_bitacora
            String sql = "INSERT INTO met_bitacora(" +
                "n_id_usuario, c_ip_modulo, c_codigo_accion, t_descripcion_acc, c_aud_uid" +
                ") VALUES(?, ?, ?, ?, ?)";
                    
            PreparedStatement pstm = cn.prepareStatement(sql);
            
            pstm.setLong(1, bitacora.getNIdUsuario());
            pstm.setString(2, bitacora.getCIpModulo());
            pstm.setString(3, bitacora.getCCodigoAccion());
            pstm.setString(4, bitacora.getTDescripcionAccion());
            pstm.setString(5, ls_audit_user);
            
            pstm.executeUpdate();
            cn.commit();
            pstm.close();
            
            System.out.println("[BitacoraServiceImpl] Registro insertado en met_bitacora " +
                "para usuario ID: " + bitacora.getNIdUsuario());
            
        } catch (Exception e) {
            System.err.println("[BitacoraServiceImpl] ERROR: " + e.getMessage());
            e.printStackTrace();
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            
            try {
                if (cn != null) cn.rollback();
            } catch (Exception ex) {
                System.err.println("[BitacoraServiceImpl] ERROR en rollback: " + ex.getMessage());
            }
        } finally {
            AccesoAsistente.clearAuditUser();
            try {
                if (cn != null) cn.close();
            } catch (Exception e) {
                System.err.println("[BitacoraServiceImpl] ERROR al cerrar conexión: " + e.getMessage());
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
    }
   
}

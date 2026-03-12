package com.ncpp.asistenteexpedientes.asistente.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ncpp.asistenteexpedientes.asistente.config.DatabaseConfig;

/**
 * Clase de acceso a la base de datos del módulo Asistente (refactorizada).
 * 
 * Versión 2.0 - Cambios principales:
 * - Integración con DatabaseConfig para cargar credenciales desde .env
 * - Soporte para contexto de auditoría (c_aud_uid)
 * - Nomenclatura técnica con prefijos gs_ (global) y ls_ (local)
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class AccesoAsistente {
    
    // Instancia de configuración (Singleton)
    private static final DatabaseConfig config = DatabaseConfig.getInstance();
    
    // ThreadLocal para mantener el usuario de auditoría por hilo
    private static ThreadLocal<String> auditUserContext = new ThreadLocal<>();

    private AccesoAsistente(){
    }

    /**
     * Obtiene una conexión a la base de datos PostgreSQL usando la configuración centralizada.
     * 
     * @return Connection objeto de conexión JDBC
     * @throws SQLException si hay errores al establecer la conexión
     */
    @SuppressWarnings("deprecation")
    public static Connection getConnection() throws SQLException {
        Connection cn = null;
        try {
            // Paso 1: Cargar el driver a memoria desde configuración
            Class.forName(config.getDbDriver()).newInstance();
            
            // Paso 2: Obtener el objeto Connection desde configuración
            cn = DriverManager.getConnection(
                config.getDbUrl(), 
                config.getDbUser(), 
                config.getDbPassword()
            );
            
            System.out.println("[AccesoAsistente] Conexión establecida a: " + config.getDbUrl());
            
        } catch (SQLException e) {
            System.err.println("[AccesoAsistente] Error SQL: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
            System.err.println("[AccesoAsistente] Driver no encontrado: " + e.getMessage());
            throw new SQLException("No se encontro el driver de la base de datos.");
        } catch (Exception e) {
            System.err.println("[AccesoAsistente] Error general: " + e.getMessage());
            throw new SQLException("No se puede establecer la conexion con la BD.");
        }
        return cn;
    }
    
    /**
     * Establece el usuario de auditoría para el hilo actual.
     * Este valor se usará en el campo c_aud_uid de las tablas auditables.
     * 
     * @param ls_usuario Usuario que realiza la operación (ej: DNI, código de usuario)
     */
    public static void setAuditUser(String ls_usuario) {
        if (ls_usuario != null && !ls_usuario.trim().isEmpty()) {
            auditUserContext.set(ls_usuario);
            System.out.println("[AccesoAsistente] Usuario de auditoría establecido: " + ls_usuario);
        } else {
            // Si no se provee usuario, usa el configurado por defecto
            auditUserContext.set(config.getAuditUser());
        }
    }
    
    /**
     * Obtiene el usuario de auditoría del contexto actual.
     * Si no está establecido, retorna el usuario por defecto de la configuración.
     * 
     * @return String usuario de auditoría
     */
    public static String getAuditUser() {
        String ls_user = auditUserContext.get();
        if (ls_user == null || ls_user.trim().isEmpty()) {
            ls_user = config.getAuditUser();
        }
        return ls_user;
    }
    
    /**
     * Limpia el contexto de auditoría del hilo actual.
     * Debe llamarse al finalizar una transacción o request.
     */
    public static void clearAuditUser() {
        auditUserContext.remove();
    }
    
    /**
     * Establece el usuario de auditoría en una conexión mediante variables de sesión.
     * Esto permite que los triggers de PostgreSQL accedan al usuario desde la sesión.
     * 
     * @param cn Conexión activa
     * @param ls_usuario Usuario de auditoría
     * @throws SQLException si hay errores al establecer la variable
     */
    public static void setSessionAuditUser(Connection cn, String ls_usuario) throws SQLException {
        if (cn != null && ls_usuario != null) {
            String sql = "SET SESSION app.audit_user = ?";
            try (PreparedStatement pstm = cn.prepareStatement(sql)) {
                pstm.setString(1, ls_usuario);
                pstm.execute();
                System.out.println("[AccesoAsistente] Variable de sesión app.audit_user establecida: " + ls_usuario);
            }
        }
    }
}

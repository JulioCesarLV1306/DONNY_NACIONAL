package com.ncpp.asistenteexpedientes.asistente.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase de configuración para la base de datos PostgreSQL refactorizada.
 * 
 * Esta clase centraliza la carga de variables de entorno desde el archivo .env
 * siguiendo el estándar de nomenclatura:
 * - gs_ = Global String (configuración global del sistema)
 * - ls_ = Local String (variables locales/temporales)
 * 
 * Patrón Singleton para garantizar una única instancia de configuración.
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class DatabaseConfig {
    
    // Singleton instance
    private static DatabaseConfig instance;
    
    // Mapa de configuraciones cargadas desde .env
    private Map<String, String> config;
    
    // Variables de configuración con valores por defecto
    private String ls_db_driver;
    private String ls_db_host;
    private String ls_db_port;
    private String ls_db_name;
    private String ls_db_user;
    private String ls_db_pass;
    private String ls_audit_user;
    private String ls_db_url;
    
    /**
     * Constructor privado (Singleton Pattern)
     */
    private DatabaseConfig() {
        config = new HashMap<>();
        loadEnvFile();
        initializeVariables();
    }
    
    /**
     * Obtiene la instancia única de DatabaseConfig
     * @return Instancia singleton de DatabaseConfig
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Carga las variables de entorno desde el archivo .env
     * Busca el archivo en el directorio raíz del proyecto
     */
    private void loadEnvFile() {
        // Intenta cargar desde el archivo del sistema
        String envFilePath = ".env";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
            System.out.println("[DatabaseConfig] Archivo .env cargado exitosamente desde: " + envFilePath);
        } catch (IOException e) {
            System.err.println("[DatabaseConfig] No se pudo cargar el archivo .env desde el sistema de archivos: " + e.getMessage());
            
            // Intenta cargar desde el classpath como fallback
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(".env");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                
                if (is != null) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        parseLine(line);
                    }
                    System.out.println("[DatabaseConfig] Archivo .env cargado exitosamente desde classpath");
                } else {
                    System.err.println("[DatabaseConfig] No se encontró archivo .env. Usando valores por defecto.");
                }
            } catch (Exception ex) {
                System.err.println("[DatabaseConfig] Error al cargar .env desde classpath: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Parsea una línea del archivo .env
     * Formato esperado: KEY=VALUE
     */
    private void parseLine(String line) {
        if (line == null || line.trim().isEmpty() || line.trim().startsWith("#")) {
            return; // Ignora líneas vacías y comentarios
        }
        
        String[] parts = line.split("=", 2);
        if (parts.length == 2) {
            String key = parts[0].trim();
            String value = parts[1].trim();
            config.put(key, value);
        }
    }
    
    /**
     * Inicializa las variables locales con los valores del archivo .env
     * o con valores por defecto si no están definidos
     */
    private void initializeVariables() {
        ls_db_driver = getConfigValue("gs_db_driver", "org.postgresql.Driver");
        ls_db_host = getConfigValue("gs_db_host", "localhost");
        ls_db_port = getConfigValue("gs_db_port", "5432");
        ls_db_name = getConfigValue("gs_db_name", "donny");
        ls_db_user = getConfigValue("gs_db_user", "postgres");
        ls_db_pass = getConfigValue("gs_db_pass", "Pj123456");
        ls_audit_user = getConfigValue("gs_audit_user", "SYSTEM_APP");
        
        // Construye la URL JDBC
        ls_db_url = String.format("jdbc:postgresql://%s:%s/%s", ls_db_host, ls_db_port, ls_db_name);
        
        System.out.println("[DatabaseConfig] Configuración inicializada:");
        System.out.println("  - Driver: " + ls_db_driver);
        System.out.println("  - URL: " + ls_db_url);
        System.out.println("  - User: " + ls_db_user);
        System.out.println("  - Audit User: " + ls_audit_user);
    }
    
    /**
     * Obtiene un valor de configuración con un valor por defecto
     */
    private String getConfigValue(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }
    
    // Getters públicos para acceso a las configuraciones
    
    public String getDbDriver() {
        return ls_db_driver;
    }
    
    public String getDbUrl() {
        return ls_db_url;
    }
    
    public String getDbUser() {
        return ls_db_user;
    }
    
    public String getDbPassword() {
        return ls_db_pass;
    }
    
    public String getAuditUser() {
        return ls_audit_user;
    }
    
    public String getDbHost() {
        return ls_db_host;
    }
    
    public String getDbPort() {
        return ls_db_port;
    }
    
    public String getDbName() {
        return ls_db_name;
    }
    
    /**
     * Recarga la configuración desde el archivo .env
     * Útil para cambios en tiempo de ejecución (no recomendado en producción)
     */
    public synchronized void reload() {
        config.clear();
        loadEnvFile();
        initializeVariables();
        System.out.println("[DatabaseConfig] Configuración recargada");
    }
}

package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import util.Mensajes;

public class AccesoAsistente {
    
    private final static String DRIVER_CLASS_NAME = "org.postgresql.Driver";

    //SANTA
    // private final static String URL = "jdbc:postgresql://10.20.208.7/ASISTENTE_SANTA";
    
    //ICA
    //private final static String URL = "jdbc:postgresql://172.17.104.247/ASISTENTE_SANTA";

    // private final static String USERNAME = "ASISTENTESANTA_ADM";
    // private final static String PASSWORD = "ADMIN7895123$$#";

    //LOCAL CENTRAL
    private final static String URL = "jdbc:postgresql://localhost/donny";
    private final static String USERNAME = "postgres";
    private final static String PASSWORD = "Pj123456";


    private AccesoAsistente(){
    }

    @SuppressWarnings("deprecation")
	public static Connection getConnection() throws SQLException {
        Connection cn = null;
        try {
            // Paso 1: Cargar el driver a memoria 
            Class.forName(DRIVER_CLASS_NAME).newInstance();
            // Paso 2: Obtener el objeto Connection 
            cn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            
            throw e;
        } catch (ClassNotFoundException e) {
            System.out.println(e);
             Mensajes.errorMensaje("No se encontro el driver de la base de datos: " + e);
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e);
             Mensajes.errorMensaje("No se puede establecer la conexion con la BD: " + e);
             System.exit(0);
        }
        return cn;
    }
    
}

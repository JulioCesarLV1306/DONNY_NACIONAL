package com.ncpp.asistenteexpedientes.sij.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccesoSij {
    /* 
    private final static String DRIVER_CLASS_NAME = "com.sybase.jdbc4.jdbc.SybDriver";
    private final static String URL = "jdbc:sybase:Tds:172.17.104.66:2811/sij11_001_14_01";
    private final static String USERNAME = "USR_CSJICA";
    private final static String PASSWORD = "sql$$";
*/
    
    private final static String DRIVER_CLASS_NAME = "com.sybase.jdbc4.jdbc.SybDriver";
    private final static String URL = "jdbc:sybase:Tds:172.17.72.73:2811/sij11_001_25_01";
    private final static String USERNAME = "usrCese";
    private final static String PASSWORD = "usrCese2025&";


    private AccesoSij(){
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
            throw new SQLException("No se encontro el driver de la base de datos.");
        } catch (Exception e) {
            throw new SQLException("No se puede establecer la conexion con la BD.");
        }
        return cn;
    }
}

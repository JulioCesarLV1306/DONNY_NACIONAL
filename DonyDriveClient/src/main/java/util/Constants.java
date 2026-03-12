package util;

public class Constants {
    
    public static final String VERSION_CLIENT = "v5.0";
    
    /*public static final String RUTA_ARCHIVO_DONY="C:/psanta/dony";
    public static final String NOMBRE_ARCHIVO_DONY="modulo.dony";
    
    public final static String RUTA_ARCHIVO_TO_PDF="C:/psanta/dony";
    public final static String NOMBRE_ARCHIVO_TO_PDF="host.2pdf";*/
    
    public static final String KEY_DISCOS_EXCLU="KEY_DISCOS_EXCLUIDOS";
    
    //public static final String IP_SERVER = "10.20.208.7";
    public static final String IP_SERVER =
            System.getenv().getOrDefault("DONY_SERVER_IP", "192.168.177.102");
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;

/**
 *
 * @author Diego Baes
 */
public class Util {

    public static String bytesToHumanReadable(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }
    
       public static String getCurrentOnlyDate(){
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    public static String getLocalIp() {
        String ip = "";
        try {
            final DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
            System.out.println(ip);
        } catch (Exception e) {
            System.err.println(e);
        }
        return ip;

    }

    public static boolean isDOCFile(String filename) {
        return filename.endsWith(".doc");
    }

    public static String crearDirectorio(String ruta) {
        File directorio = new File(ruta);
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                System.out.println("Directorio creado: " + ruta);
            } else {
                System.out.println("Error al crear directorio" + ruta);
            }
        }
        return ruta;
    }

    public static String generarKeyDescarga(String fechaActual, String ipModulo, String dniPersona, String nUnico, int nIncidente, String keyEleccion, String[] fechasElegidas) {
        //String letraUnidad=drive.getLetraUnidad().substring(0, drive.getLetraUnidad().length()-1);
        return fechaActual + "/" + ipModulo + "/" + dniPersona + "/" + nUnico + "/" + nIncidente + "/" + keyEleccion.toUpperCase() + "/"
                + generarConcatenadoFechas(fechasElegidas);
    }

    private static String generarConcatenadoFechas(String[] fechasElegidas) {
        String fechas = "";
        for (String fechaElegida : fechasElegidas) {
            fechas += fechaElegida.replace("-", "");
        }
        return fechas;
    }
    
    public static int getPorcentajeCopia(int conteoCopia, int totalCopia){
        return totalCopia == 0 ? 0 : (conteoCopia*100)/totalCopia;
    }
    

    /*public static ResponseEstadoDescarga setEstadoCopiando(String keyDescarga, int porcentaje, int descargados, int totalArchivos) {
        ResponseEstadoDescarga rsp = new ResponseEstadoDescarga("copiando", porcentaje, descargados, totalArchivos);
        Memory.put(keyDescarga, rsp);
        return rsp;
    }

    public static ResponseEstadoDescarga setEstadoErrorCopia(String keyDescarga) {
        ResponseEstadoDescarga rsp = new ResponseEstadoDescarga("error-copia", 0, 0, 0);
        Memory.put(keyDescarga, rsp);
        return rsp;
    }

    public static ResponseEstadoDescarga setEstadoCompletoCopia(String keyDescarga, int descargados, int totalArchivos) {
        ResponseEstadoDescarga rsp = new ResponseEstadoDescarga("completo-copia", 100, descargados, totalArchivos);
        Memory.put(keyDescarga, rsp);
        return rsp;
    }

    public static ResponseEstadoDescarga getMemoryEstadoDescarga(String keyDescarga) {
        return (ResponseEstadoDescarga) Memory.get(keyDescarga);
    }*/

}

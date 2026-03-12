package com.ncpp.asistenteexpedientes.util;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import com.ncpp.asistenteexpedientes.sij.database.AccesoSij;
import com.ncpp.asistenteexpedientes.sij.entity.Fecha;

import org.springframework.data.domain.Pageable;


public class Util {

    private Util() {
    }

    public static String getCurrentDate(){
        java.util.Date date = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    public static String completeNumeroExpediente(int numero) {
        return String.format("%05d", numero);
    }

    /*public static boolean esNuevaBusqueda(String key) {
        return !Memory.containsKey(key);
    }

    public static String generateKeyFindExpediente(int numero, int anio, int cuaderno) {
        return "EXP:" + numero + "ANIO:" + anio + "CUAD:" + cuaderno;
    }

    public static String generateKeyFechas(String formatoExpediente, String eleccion) {
        return "keyfechas" + eleccion + "_" + formatoExpediente;
    }*/

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

    public static int countRowsFechas(String where_sql,String campo_fecha,Object[] parameters){
        int count_rows=0;
        Connection cn = null;
        List<Fecha> lista = new ArrayList<Fecha>();
        try{
            cn = AccesoSij.getConnection();
            String sql = "SELECT "+campo_fecha+" AS fecha " + where_sql + " ORDER BY DATEFORMAT("+campo_fecha+", 'YYYY-MM-DD') ";
            PreparedStatement pstm = cn.prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    pstm.setObject(i + 1, parameters[i]);
                }
            }
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                Fecha actualFecha = new Fecha();
                actualFecha.setFecha(rs.getDate("fecha"));
              
                if (!isFechaDuplicada(lista, actualFecha)) {
                    count_rows++;
                    lista.add(actualFecha);
                    
                }
            }
            rs.close();
            pstm.close();
        }catch(Exception e ){
            System.out.println(e);
            LogDony.write("UTIL"+" - ERROR: "+e);
        }
    
        return count_rows;
    }

    public static int countRows(String where_sql, Object[] parameters) {
        Connection cn = null;
        int count = 0;
        try {
            cn = AccesoSij.getConnection();
            String sql = "SELECT count(*) AS rowcount " + where_sql;
            PreparedStatement pstm = cn.prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    pstm.setObject(i + 1, parameters[i]);
                }
            }
            ResultSet rs = pstm.executeQuery();
            rs.next();
            count = rs.getInt("rowcount");
            rs.close();
            pstm.close();

        } catch (Exception e) {
            System.out.println(e);
            LogDony.write("UTIL"+" - ERROR: "+e);
        } finally {
            try {
                cn.close();
            } catch (Exception e2) {
                LogDony.write("UTIL"+" - ERROR: "+e2);
            }
        }
        return count;
    }

    public static String getRutaByNombreActa(String nombreActa, String n_audiencia) {
        String anio = nombreActa.substring(0, 4);
        String mes = nombreActa.substring(4, 6);

        int index = nombreActa.indexOf('_');
        String ex = nombreActa.substring(8, index);
        return anio + "/" + mes + "/" + ex + "/" + n_audiencia + "/";
    }

    public static String completeWhereSqlFechas(String campo, String[] fechas) {
        String sql = "";
        if (fechas != null) {
            for (int i = 0; i < fechas.length; i++) {
                sql += i == 0 ? " ( " : " or ";
                sql += " DATEFORMAT(" + campo + ", 'YYYY-MM-DD') = '" + fechas[i] + "' ";
                if (i == fechas.length - 1)
                    sql += ")";
            }
        }
        return sql;
    }

    public static String generarKeyDescarga(String fechaActual, String ipModulo, String dniPersona, String nUnico , int nIncidente, String keyEleccion, String [] fechasElegidas) {
       //String letraUnidad=drive.getLetraUnidad().substring(0, drive.getLetraUnidad().length()-1);
        return fechaActual+"/"+ipModulo+"/"+dniPersona+"/" +nUnico + "/"+ nIncidente+"/" + keyEleccion.toUpperCase() + "/"
                + generarConcatenadoFechas(fechasElegidas);
    }

    private static String generarConcatenadoFechas(String[] fechasElegidas){
        String fechas = "";
        for (String fechaElegida : fechasElegidas) {
            fechas+=fechaElegida.replace("-", "");
        }
        return fechas;
    }

    public static String generarRutaSalidaFinal( String formatoExpediente, String keyEleccion, String fechaDocumento) {
         return  "ASISTENTE-DONNY\\" + formatoExpediente + "\\" + keyEleccion.toUpperCase() + "\\"
                 + fechaDocumento;
     }
/*
    public static Descarga setEstadoDescargando(String keyDescarga, int porcentaje, int archivosDescargados, int totalArchivos) {
        ResponseEstadoDescarga rsp = new ResponseEstadoDescarga("descargando", porcentaje, archivosDescargados, totalArchivos);
        Memory.put(keyDescarga, rsp);
        DescargaServiceImpl descargaServiceImpl = new DescargaServiceImpl();
        descargaServiceImpl.
        return rsp;
    }

    public static Descarga setEstadoErrorDescarga(String keyDescarga) {
        ResponseEstadoDescarga rsp = new ResponseEstadoDescarga("error-descarga", 0, 0, 0);
        Memory.put(keyDescarga, rsp);
        return rsp;
    }

    public static Descarga setEstadoCompletoDescarga(String keyDescarga, int archivosDescargados, int totalArchivos) {
        ResponseEstadoDescarga rsp = new ResponseEstadoDescarga("completo-descarga", 100, archivosDescargados, totalArchivos);
        Memory.put(keyDescarga, rsp);
        return rsp;
    }

    public static Descarga setEstadoFaltaEspacio(String keyDescarga){
        ResponseEstadoDescarga rsp = new ResponseEstadoDescarga("falta-espacio", 0, 0, 0);

        return null;
    }
*/
    public static int getPorcentajeDescarga(int archivosDescargados, int totalArchivos){
        return totalArchivos == 0 ? 0 : (archivosDescargados*100)/totalArchivos;
    }

    public static String crearDirectorio(String ruta) {
        File directorio = new File(ruta);
        if (!directorio.exists()) {
            if (!directorio.mkdirs()) {
                System.out.println("Error al crear directorio" + ruta);
            }
        }
        return ruta;
    }

    public static Fecha createFecha(ResultSet rs) {
        Fecha fecha = new Fecha();
        try {
            fecha.setFecha(rs.getDate("fecha"));
            fecha.setSumilla(rs.getString("sumilla"));

        } catch (Exception e) {
            fecha = null;
            System.out.println(e);
            LogDony.write("UTIL"+" - ERROR: "+e);
        }
        return fecha;
    }

    public static String getFechaToFormatString(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(fecha);
    }

    public static boolean isFechaDuplicada(List<Fecha> lista, Fecha actualFecha) {
        if (lista.size() > 0) {
            int lastIndex = lista.size() - 1;
            String strUltimaFecha = Util.getFechaToFormatString(lista.get(lastIndex).getFecha());
            if (Util.getFechaToFormatString(actualFecha.getFecha()).equals(strUltimaFecha)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static List<Fecha> getListaFechas(ResultSet rs, Pageable page) {
        List<Fecha> lista_temp = new ArrayList<>();
        List<Fecha> lista_final = new ArrayList<>();
        int number_row = 0;
        int cantidad_duplicados=1;
        try {
            while (rs.next() && lista_final.size() < page.getPageSize()) {
                Fecha actualFecha = Util.createFecha(rs);
                if(lista_final.size() <= 0){
                    if(!Util.isFechaDuplicada(lista_temp, actualFecha)){
                        lista_temp.add(actualFecha);
                        number_row++;
                    }
                    if(number_row > page.getOffset()){
                        cantidad_duplicados=1;
                        lista_final.add(actualFecha);
                    }

                }else{
                    if ( Util.isFechaDuplicada(lista_final, actualFecha)){
                        if(number_row > page.getOffset()){
                            cantidad_duplicados++;
                            lista_final.get(lista_final.size() - 1).setSumilla(cantidad_duplicados+ " ARCHIVOS ENCONTRADOS");
                        }
                    }else{
                        number_row++;
                        if(number_row > page.getOffset()){
                            cantidad_duplicados=1;
                            lista_final.add(actualFecha);
                        }
                    }
                }
               
            }
        } catch (SQLException e) {
            lista_final = null;
            System.out.println(e);
            LogDony.write("UTIL"+" - ERROR: "+e);
        }
        return lista_final;
    }

    public static boolean isDOCFile(String filename) {
        return filename.endsWith(".doc");
    }

    public static int getMaxId(String nombreCampo, String nombreTabla, Connection cn) {
        // Obtenemos Id de Acceso recien Creado
        int idMax = 0;
        try {

            String sql = "SELECT Max("+nombreCampo+") FROM "+nombreTabla;
            PreparedStatement pstm = cn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                idMax = rs.getInt("Max");
            }
            rs.close();
            pstm.close();
        } catch (Exception e) {
            idMax=0;
            LogDony.write("UTIL"+" - ERROR: "+e);
        }
        return idMax;
    }
    

}

package com.ncpp.asistenteexpedientes.util;

import java.io.IOException;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.ncpp.asistenteexpedientes.asistente.service.EstadisticasService;
import com.ncpp.asistenteexpedientes.asistente.service.impl.EstadisticasServiceImpl;
import com.ncpp.asistenteexpedientes.sij.entity.ServidorFtp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.pdfbox.pdmodel.PDDocument;

//import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
//import java.io.OutputStream;

public class FTPDownloader {
    public static long size(ServidorFtp servidorFtp, String routeFile, String nameFile) {
        FTPClient ftpClient = new FTPClient();

        long size = 0;
        try {

            if (!routeFile.endsWith("/")) {
                routeFile = routeFile + "/";
            }

            ftpClient.connect(servidorFtp.getIpServer());
            ftpClient.login(servidorFtp.getNombreUsuario(), servidorFtp.getClaveUsuario());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            FTPFile[] files = ftpClient.listFiles(routeFile + nameFile);
            if (files != null) {
                size = files[0].getSize();
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            LogDony.write("FTPDOWNLOADER" + " - ERROR: " + ex);
            size = 0;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                LogDony.write("FTPDOWNLOADER" + " - ERROR: " + ex);
            }
        }
        return size;
    }

    public static boolean download(ServidorFtp servidorFtp, String routeFile, String nameFile, String rutaSalida,
            String keyEstadoDescarga, long idModulo) {
        FTPClient ftpClient = new FTPClient();
        boolean success = false;
        try {

            /*System.out.println("servidorFtp: "+ servidorFtp.toString()); 
            System.out.println("routeFile: "+routeFile);
            System.out.println("nameFile: "+nameFile);*/

            EstadisticasServiceImpl estadisticasServices = new EstadisticasServiceImpl();

            if (!routeFile.endsWith("/")) {
                routeFile = routeFile + "/";
            }

            ftpClient.connect(servidorFtp.getIpServer());
            ftpClient.login(servidorFtp.getNombreUsuario(), servidorFtp.getClaveUsuario());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            long fileSize = 0;
            FTPFile[] files = ftpClient.listFiles(routeFile + nameFile);
            if (files != null) {
                fileSize = files[0].getSize();
            }

            InputStream inputStream = ftpClient.retrieveFileStream(routeFile + nameFile);

            File archivoFTP = new File(Util.crearDirectorio(rutaSalida) + "\\" + nameFile);
            archivoFTP.createNewFile();

            try (FileOutputStream out = new FileOutputStream(archivoFTP)) {
                IOUtils.copy(inputStream, out);
            }

            if (nameFile.endsWith(".doc")) {
                File archivoPDF = new File(Util.crearDirectorio(rutaSalida) + "\\" + nameFile.replace(".doc", ".pdf"));
                archivoPDF = docToPDF(archivoFTP, archivoPDF);
                estadisticasServices.aumentarHojas(idModulo, getTotalPagesPDF(archivoPDF));
            }

            if (nameFile.endsWith(".pdf")) {
                estadisticasServices.aumentarHojas(idModulo, getTotalPagesPDF(archivoFTP));
            }

            inputStream.close();
            success = true;

            try {
                EstadisticasService estadisticasService = new EstadisticasServiceImpl();
                estadisticasService.aumentarBytes(idModulo, fileSize);
            } catch (Exception e) {
                System.out.println("Error aumento bytes: " + e.getMessage());
            }
         
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            success = false;
            ex.printStackTrace();
            LogDony.write("FTPDOWNLOADER" + " - ERROR: " + ex);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                LogDony.write("FTPDOWNLOADER" + " - ERROR: " + ex);
            }
        }
        return success;
    }

    private static int getTotalPagesPDF(File in) {
        PDDocument doc = null;
        int pages = 0;
        try {
            doc = PDDocument.load(in);
            pages = doc.getNumberOfPages();
            doc.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            LogDony.write("FTPDOWNLOADER" + " - ERROR: " + e);
        }
        return pages;
    }

    /*
     * private static File stream2file (InputStream in){ File tempFile = null; try{
     * tempFile = File.createTempFile("stream2file", ".tmp");
     * tempFile.deleteOnExit(); FileOutputStream out = new
     * FileOutputStream(tempFile); IOUtils.copy(in, out); }catch(Exception e){
     * System.out.println("Error: "+e); e.printStackTrace(); } return tempFile; }
     */

    // CONVERTIDOR OPENOFFICE
    private static File docToPDF(File in, File out) {
        File f = null;
        try {
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
            connection.connect();
            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            converter.convert(in, out);
            // close the connection
            connection.disconnect();
            in.delete();
            f = out;
        } catch (Exception e) {
            System.out.println(e);
            LogDony.write("FTPDOWNLOADER" + " - ERROR: " + e);
        }
        return f;
    }


}

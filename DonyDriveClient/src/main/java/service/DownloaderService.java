package service;

import model.Bitacora;
import model.Descarga;
import model.Usuario;
import util.Constants;
import util.Util;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Diego Baes
 */
public class DownloaderService {
    
    public Descarga copiar(String nUnico, int nIncidente, String[] fechas, String keyEleccion, String driveLetra,
            String moduloIP, String dniPersona, 
            String nombrePersona , String usuarioModulo , String expediente) {

        String keyDescarga = Util.generarKeyDescarga(Util.getCurrentOnlyDate(), moduloIP, dniPersona,
        nUnico, nIncidente, keyEleccion, fechas);       
        DescargaService descargaService = new DescargaService();
        Descarga descargaEncontrada = descargaService.find(keyDescarga);
        if (descargaEncontrada == null || descargaEncontrada.getNIdDescarga() == null || descargaEncontrada.getNIdDescarga() == 0) {
            descargaEncontrada = descargaService.findByContext(moduloIP, dniPersona, nUnico, nIncidente, keyEleccion, fechas);
        }
        if (descargaEncontrada == null || descargaEncontrada.getNIdDescarga() == null || descargaEncontrada.getNIdDescarga() == 0) {
            Descarga errorDescarga = new Descarga();
            errorDescarga.setCKeyDescarga(keyDescarga);
            errorDescarga.setXEstado(DescargaService.ESTADO_ERROR_COPIA);
            errorDescarga.setXMensajeFinal("No se encontró la descarga previa para iniciar la copia en USB");
            return errorDescarga;
        }

        final Descarga descarga = descargaEncontrada;

        Thread hiloCopiaArchivos = new Thread() {
            @Override
            public void run() {
                try {
                    String destino = driveLetra.endsWith("\\") ? driveLetra : driveLetra + "\\";
                    String sourcePath = "\\\\" + Constants.IP_SERVER + "\\Asistente_Dony\\descargas\\"
                            + descarga.getNIdDescarga() + "\\*";
                    String command = "xcopy \"" + sourcePath + "\" \"" + destino + "\" /E /I /Y";
                    
                    ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", command);
                    
                    System.out.println("command: "+command);
                    
                    
                    try {
                        // Crear/obtener usuario antes de insertar bitácora
                        UsuarioService usuarioService = new UsuarioService();
                        Usuario usuario = usuarioService.createIfNotExists(dniPersona, nombrePersona);
                        
                        Bitacora bitacora = new Bitacora();
                        BitacoraService bitacoraService = new BitacoraService();
                        bitacora.setNIdUsuario(usuario.getNIdUsuario());
                        bitacora.setCCodigoAccion("COPIA_ARCHIVOS");
                        bitacora.setCIpModulo(moduloIP);
                        bitacora.setTDescripcionAccion("EMPEZO SU PROCESO DE COPIADO "
                                + " EN EL DISPOSITIVO " + driveLetra
                                + " DEL EXPEDIENTE " + expediente + " LOS ARCHIVOS " + keyEleccion + " CON FECHAS " + String.join(", ", fechas));
                        bitacora.setCAudUid(dniPersona);
                        bitacoraService.create(bitacora);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    Process p = pb.start();

                    p.waitFor();
                    if (p.exitValue() != 0) {
                        System.out.println("Error! " + p.exitValue());
                       System.out.println("Error! " + p.getErrorStream());
                       descargaService.updateCopia(descarga, DescargaService.ESTADO_ERROR_COPIA, 0, 1);
                    } else {
                        descargaService.updateCopia(descarga, DescargaService.ESTADO_COMPLETO_COPIA, 1, 1);
                    }
                    
                     System.out.println("Output! " + p.getOutputStream());

                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                    descargaService.updateCopia(descarga, DescargaService.ESTADO_ERROR_COPIA, 0, 1);
                }
            }

        };
        hiloCopiaArchivos.start();

        return descargaService.updateCopia(descarga, DescargaService.ESTADO_COPIANDO, 0, 0);
    }
    
     public Descarga consultar(String ipModulo, String dniPersona, String nUnico, int nIncidente,String keyEleccion, String[] fechas) {
        String keyDescarga = Util.generarKeyDescarga(Util.getCurrentOnlyDate(), ipModulo, dniPersona,
        nUnico,nIncidente, keyEleccion, fechas);
        DescargaService descargaService = new DescargaService();
        Descarga descarga = descargaService.find(keyDescarga);
        if (descarga == null || descarga.getNIdDescarga() == null || descarga.getNIdDescarga() == 0) {
            descarga = descargaService.findByContext(ipModulo, dniPersona, nUnico, nIncidente, keyEleccion, fechas);
        }
        return descarga;
    }
    
}

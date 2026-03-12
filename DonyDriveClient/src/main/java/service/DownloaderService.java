package service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
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
                    String destino = normalizarDestinoDrive(driveLetra);
                    System.out.println("driveLetra raw: [" + driveLetra + "]");
                    System.out.println("driveLetra normalizado: [" + destino + "]");
                    if (destino == null) {
                        System.out.println("Error! driveLetra inválido");
                        descargaService.updateCopia(descarga, DescargaService.ESTADO_ERROR_COPIA, 0, 1);
                        return;
                    }

                    File destinoDrive = new File(destino);
                    if (!destinoDrive.exists()) {
                        System.out.println("Error! la unidad destino no existe: " + destino);
                        descargaService.updateCopia(descarga, DescargaService.ESTADO_ERROR_COPIA, 0, 1);
                        return;
                    }

                    String sourceDir = resolverOrigenCopia(descarga.getNIdDescarga());
                    if (sourceDir == null) {
                        System.out.println("Error! no se encontró directorio de origen para ID descarga: "
                                + descarga.getNIdDescarga());
                        descargaService.updateCopia(descarga, DescargaService.ESTADO_ERROR_COPIA, 0, 1);
                        return;
                    }

                    ProcessBuilder pb = new ProcessBuilder("robocopy", sourceDir, destino, "/E", "/R:1", "/W:1", "/NFL", "/NDL");
                    pb.redirectErrorStream(true);

                    System.out.println("robocopy source: " + sourceDir);
                    System.out.println("robocopy destino: " + destino);
                    
                    
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
                    boolean finished = p.waitFor(15, TimeUnit.MINUTES);
                    if (!finished) {
                        p.destroyForcibly();
                        System.out.println("Error! timeout de copia (15 min)");
                        descargaService.updateCopia(descarga, DescargaService.ESTADO_ERROR_COPIA, 0, 1);
                        return;
                    }

                    String processOutput = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    int exitCode = p.exitValue();
                    if (exitCode > 7) {
                        System.out.println("Error! " + p.exitValue());
                        System.out.println("robocopy output: " + processOutput);
                       descargaService.updateCopia(descarga, DescargaService.ESTADO_ERROR_COPIA, 0, 1);
                    } else {
                        System.out.println("robocopy output: " + processOutput);
                        descargaService.updateCopia(descarga, DescargaService.ESTADO_COMPLETO_COPIA, 1, 1);
                    }

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

    private String normalizarDestinoDrive(String driveLetra) {
        if (driveLetra == null) {
            return null;
        }
        String valor = driveLetra.trim().replace("\"", "").replace("/", "\\");
        Pattern patron = Pattern.compile("([A-Za-z]):");
        Matcher matcher = patron.matcher(valor);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1).toUpperCase() + ":\\";
    }

    private String resolverOrigenCopia(Long nIdDescarga) {
        if (nIdDescarga == null) {
            return null;
        }

        String rutaLocalBase = System.getenv().getOrDefault("DONY_DESCARGAS_DIR", "D:\\Asistente_Dony\\descargas");
        String sourceLocal = rutaLocalBase + "\\" + nIdDescarga;
        if (new File(sourceLocal).exists()) {
            return sourceLocal;
        }

        String sourceRed = "\\\\" + Constants.IP_SERVER + "\\Asistente_Dony\\descargas\\" + nIdDescarga;
        if (new File(sourceRed).exists()) {
            return sourceRed;
        }

        return null;
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

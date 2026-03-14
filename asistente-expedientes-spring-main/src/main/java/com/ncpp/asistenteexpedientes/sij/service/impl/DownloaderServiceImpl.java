package com.ncpp.asistenteexpedientes.sij.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ncpp.asistenteexpedientes.asistente.dto.Drive;
import com.ncpp.asistenteexpedientes.asistente.entity.Descarga;
import com.ncpp.asistenteexpedientes.asistente.service.impl.DescargaServiceImpl;
import com.ncpp.asistenteexpedientes.asistente.service.impl.EstadisticasServiceImpl;
import com.ncpp.asistenteexpedientes.sij.entity.Archivo;
import com.ncpp.asistenteexpedientes.sij.payload.response.ResponseTamanioDescarga;
import com.ncpp.asistenteexpedientes.sij.service.specs.DownloaderService;
import com.ncpp.asistenteexpedientes.util.Constants;
import com.ncpp.asistenteexpedientes.util.FTPDownloader;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

public class DownloaderServiceImpl implements DownloaderService {

   
    @Override
    public List<ResponseTamanioDescarga> getTamanioDescarga(String nUnico, int nIncidente, String[] fechas,
            String[] keysEleccion) {
        List<ResponseTamanioDescarga> lista = new ArrayList<>();
        for (String key : keysEleccion) {
            long size = 0;
            switch (key) {
                case "actas":
                    ActaServiceImpl actaService = new ActaServiceImpl();
                    for (Archivo archivo : actaService.buscarPorFechas(nUnico, nIncidente, fechas)) {
                        size += FTPDownloader.size(archivo.getServidorFtp(), archivo.getRuta(), archivo.getNombre());
                    }
                    lista.add(new ResponseTamanioDescarga(key, size, Util.bytesToHumanReadable(size)));
                    break;
                case "videos":
                    VideoServiceImpl videoService = new VideoServiceImpl();
                    for (Archivo archivo : videoService.buscarPorFechas(nUnico, nIncidente, fechas)) {
                        size += FTPDownloader.size(archivo.getServidorFtp(), archivo.getRuta(), archivo.getNombre());
                    }
                    lista.add(new ResponseTamanioDescarga(key, size, Util.bytesToHumanReadable(size)));
                    break;
                case "resoluciones":
                    ResolucionServiceImpl resolucionService = new ResolucionServiceImpl();
                    for (Archivo archivo : resolucionService.buscarPorFechas(nUnico, nIncidente, fechas)) {
                        size += FTPDownloader.size(archivo.getServidorFtp(), archivo.getRuta(), archivo.getNombre());
                    }
                    lista.add(new ResponseTamanioDescarga(key, size, Util.bytesToHumanReadable(size)));
                    break;
                case "documentosdigitalizados":
                    DocumentoDigitalizadoServiceImpl digitalizadoService = new DocumentoDigitalizadoServiceImpl();
                    for (Archivo archivo : digitalizadoService.buscarPorFechas(nUnico, nIncidente, fechas)) {
                        size += FTPDownloader.size(archivo.getServidorFtp(), archivo.getRuta(), archivo.getNombre());
                    }
                    lista.add(new ResponseTamanioDescarga(key, size, Util.bytesToHumanReadable(size)));
                    break;
                case "depositos":
                    size=1000000; // simulacion pesa 1MB
                    lista.add(new ResponseTamanioDescarga(key, size, Util.bytesToHumanReadable(size)));
                    break;
                default:
                    break;
            }
        }
        System.out.println(keysEleccion);
        System.out.println(lista.toString());
        return lista;
    }

    @Override
    public Descarga descargar(String formatoExpediente, String nUnico, int nIncidente,String[] fechas, String keyEleccion, long tamanio,
            Drive drive, String ipModulo, long idModulo, String dniPersona) {
        String keyDescarga = Util.generarKeyDescarga(Util.getCurrentDate(), ipModulo, dniPersona,
        nUnico,nIncidente, keyEleccion, fechas);

         EstadisticasServiceImpl estadisticasService = new EstadisticasServiceImpl();      
         DescargaServiceImpl descargaService = new DescargaServiceImpl();                      
        Descarga descargaResponse = new Descarga();


        if (drive.getEspacioLibre() > tamanio) {
            switch (keyEleccion) {
                case "actas":
                    Thread hiloActas = new Thread() {
                        @Override
                        public void run() {
                            int actasDescargados = 0;
                          
                            try {

                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,0,0);

                                ActaServiceImpl actaService = new ActaServiceImpl();
                                List<Archivo> lActas = actaService.buscarPorFechas(nUnico, nIncidente, fechas);

                                for (int i = 0; i < lActas.size(); i++) {

                                    Descarga descarga = descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,actasDescargados,lActas.size());
                                   
                                    String rutaDescarga = Constants.RUTA_DESCARGA_ARCHIVOS + "\\"
                                            + descarga.getNIdDescarga() + "\\" + Util.generarRutaSalidaFinal(
                                                    formatoExpediente, keyEleccion, lActas.get(i).getFecha());

                                    boolean success = FTPDownloader.download(lActas.get(i).getServidorFtp(),
                                            lActas.get(i).getRuta(), lActas.get(i).getNombre(), rutaDescarga, keyDescarga, idModulo);

                                    if (success)
                                        actasDescargados++;

                                }
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_COMPLETO_DESCARGA, actasDescargados, lActas.size());
                                estadisticasService.aumentarActas(idModulo, lActas.size());
                            } catch (Exception e) {
                                System.out.println(e);
                                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_ERROR_DESCARGA, 0, 0);
                            }

                        }
                    };
                    hiloActas.start();
                    descargaResponse=  getDescargaInDescargando(keyDescarga);
                    break;
                case "videos":
                    Thread hiloVideos = new Thread() {
                        @Override
                        public void run() {
                            int videosDescargados = 0;
                            try {
                                
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,0,0);
                                VideoServiceImpl videoService = new VideoServiceImpl();
                                List<Archivo> lVideos = videoService.buscarPorFechas(nUnico, nIncidente, fechas);

                                for (int i = 0; i < lVideos.size(); i++) {

                                    Descarga descarga = descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,videosDescargados,lVideos.size());

                                    String rutaDescarga = Constants.RUTA_DESCARGA_ARCHIVOS + "\\"
                                            + descarga.getNIdDescarga() + "\\" + Util.generarRutaSalidaFinal(
                                                    formatoExpediente, keyEleccion, lVideos.get(i).getFecha());

                                    boolean success = FTPDownloader.download(lVideos.get(i).getServidorFtp(),
                                            lVideos.get(i).getRuta(), lVideos.get(i).getNombre(), rutaDescarga, keyDescarga, idModulo);

                                    if (success)
                                        videosDescargados++;

                                }

                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_COMPLETO_DESCARGA, videosDescargados, lVideos.size());
                                estadisticasService.aumentarVideos(idModulo, videosDescargados);
                            } catch (Exception e) {
                                System.out.println(e);
                                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_ERROR_DESCARGA, 0, 0);
                            }

                        }
                    };
                    hiloVideos.start();
                    descargaResponse= getDescargaInDescargando(keyDescarga);
                    break;
                case "resoluciones":
                    Thread hiloResoluciones = new Thread() {
                        @Override
                        public void run() {
                            int resolucionesDescargadas = 0;
                            try {
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,0,0);
                                ResolucionServiceImpl resolucionService = new ResolucionServiceImpl();
                                List<Archivo> lResoluciones = resolucionService.buscarPorFechas(nUnico, nIncidente,
                                        fechas);

                                for (int i = 0; i < lResoluciones.size(); i++) {

                                    Descarga descarga = descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,resolucionesDescargadas,lResoluciones.size());
                                    String rutaDescarga = Constants.RUTA_DESCARGA_ARCHIVOS + "\\"
                                            + descarga.getNIdDescarga() + "\\" + Util.generarRutaSalidaFinal(
                                                    formatoExpediente, keyEleccion, lResoluciones.get(i).getFecha());

                                    boolean success = FTPDownloader.download(lResoluciones.get(i).getServidorFtp(),
                                            lResoluciones.get(i).getRuta(), lResoluciones.get(i).getNombre(), rutaDescarga,keyDescarga, idModulo);

                                    if (success)
                                        resolucionesDescargadas++;

                                }

                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_COMPLETO_DESCARGA, resolucionesDescargadas, lResoluciones.size());
                                estadisticasService.aumentarResoluciones(idModulo, lResoluciones.size());
                            } catch (Exception e) {
                                System.out.println(e);
                                LogDony.write(this.getClass().getName()+" - ERROR: "+e);                               
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_ERROR_DESCARGA, 0, 0);
                            }

                        }
                    };
                    hiloResoluciones.start();
                    descargaResponse= getDescargaInDescargando(keyDescarga);
                    break;
                case "documentosdigitalizados":
                    Thread hiloDigitalizados = new Thread() {
                        @Override
                        public void run() {
                            int digitalizadosDescargados = 0;
                            try {
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,0,0);

                                DocumentoDigitalizadoServiceImpl digitalizadosService = new DocumentoDigitalizadoServiceImpl();
                                List<Archivo> lDigitalizados = digitalizadosService.buscarPorFechas(nUnico, nIncidente,
                                        fechas);

                                for (int i = 0; i < lDigitalizados.size(); i++) {

                                    Descarga descarga = descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO,digitalizadosDescargados,lDigitalizados.size());
                                    String rutaDescarga = Constants.RUTA_DESCARGA_ARCHIVOS + "\\"
                                            + descarga.getNIdDescarga() + "\\" + Util.generarRutaSalidaFinal(
                                                    formatoExpediente, keyEleccion, lDigitalizados.get(i).getFecha());

                                    boolean success = FTPDownloader.download(lDigitalizados.get(i).getServidorFtp(),
                                            lDigitalizados.get(i).getRuta(), lDigitalizados.get(i).getNombre(), rutaDescarga,keyDescarga, idModulo);

                                    if (success)
                                        digitalizadosDescargados++;

                                }

                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_COMPLETO_DESCARGA, digitalizadosDescargados, lDigitalizados.size());
                                estadisticasService.aumentarDocumentos(idModulo, lDigitalizados.size());
                            } catch (Exception e) {
                                System.out.println(e);
                                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_ERROR_DESCARGA, 0, 0);
                            }
                        }
                    };
                    hiloDigitalizados.start();
                    descargaResponse=  getDescargaInDescargando(keyDescarga);
                    break;
                case "depositos":
                    Thread hiloDepositos = new Thread() {
                        @Override
                        public void run() {
                            
                            DepositoServiceImpl depositoService = new DepositoServiceImpl();
                            Descarga descarga = descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_DESCARGANDO, 0, 1);

                            String rutaDescarga = Constants.RUTA_DESCARGA_ARCHIVOS + "\\"
                            + descarga.getNIdDescarga() + "\\" + Util.generarRutaSalidaFinal(
                                    formatoExpediente, keyEleccion, Util.getCurrentDate());

                            boolean success  = depositoService.generarReporte(keyDescarga,rutaDescarga,nUnico, nIncidente,formatoExpediente);
                            if(success){
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_COMPLETO_DESCARGA, 1, 1);
                            } else{
                                descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_ERROR_DESCARGA, 0, 0);
                            }
                        }};
                    hiloDepositos.start();
                    descargaResponse=  getDescargaInDescargando(keyDescarga);
                    break;
                default:
                    descargaResponse=descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_ERROR_DESCARGA, 0, 0);
                    break;
            }
        } else {
            descargaResponse= descargaService.createOrUpdateDescarga(keyDescarga, DescargaServiceImpl.ESTADO_FALTA_ESPACIO, 0, 0);
        }   

        return descargaResponse;

    }

    @Override
    public Descarga consultar(String ipModulo, String dniPersona, String nUnico, int nIncidente,String keyEleccion, String[] fechas) {
        String keyDescarga = Util.generarKeyDescarga(Util.getCurrentDate(), ipModulo, dniPersona,
        nUnico,nIncidente, keyEleccion, fechas);
        DescargaServiceImpl descargaServiceImpl = new DescargaServiceImpl();
        Descarga descarga = new Descarga();
        try {
            descarga = descargaServiceImpl.find(keyDescarga);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(descarga.toString());
        return descarga;
    }

    private Descarga getDescargaInDescargando(String keyDescarga){
        Descarga descarga = new Descarga();
        descarga.setNIdDescarga(0L);
        descarga.setCKeyDescarga(keyDescarga);
        descarga.setXEstado(DescargaServiceImpl.ESTADO_DESCARGANDO);
        descarga.setNPorcentajeDescarga(0);
        descarga.setNConteoDescarga(0);
        descarga.setNTotalDescarga(0);
        return descarga;
    }

 /* 
    @Override
    public Descarga copiar(String nUnico, int nIncidente, String[] fechas, String keyEleccion, Drive drive,
            Modulo modulo, String dniPersona) {

    
       
       EN DESHUSO, AHORA SE COPIA DIRECTAMENTE DESDE DONNYDRIVECLIENT.JAR

       String keyDescarga = Util.generarKeyDescarga(Util.getCurrentDate(), modulo.getPcIp(), dniPersona,
        nUnico, nIncidente, keyEleccion, fechas);       


        Thread hiloCopiaArchivos = new Thread() {
            @Override
            public void run() {

                try {
                    //String command = Constants.RUTA_ARCHIVOS_CONF + "\\" + Constants.NOMBRE_BAT_COPY;
                   
                    ProcessBuilder pb = new ProcessBuilder();

                    DescargaServiceImpl descargaService = new DescargaServiceImpl();
                    Descarga descarga = descargaService.find(keyDescarga);

                    String command = "PsExec \\\\"+modulo.getPcIp()+" -u "+modulo.getPcUsuario()
                    +" -p "+modulo.getPcClave()+" xcopy \\\\172.17.104.247\\Asistente_Dony\\descargas\\"+descarga.getIdDescarga()+"\\* "+drive.getLetraUnidad()+" /S /Y ";

                    /*pb.command(command, modulo.getPcIp(), modulo.getPcUsuario(), modulo.getPcClave(),
                            String.valueOf(descarga.getIdDescarga()), drive.getLetraUnidad());
                    pb.command("cmd.exe","/C",command);
                            //drive.getLetraUnidad()
                    System.out.println("command: "+command);
                    System.out.println("ip: "+modulo.getPcIp());
                    System.out.println("usuario: "+modulo.getPcUsuario());
                    System.out.println("clave: "+modulo.getPcClave());
                    System.out.println("id_descarga: "+descarga.getIdDescarga());
                    System.out.println("drive: "+drive.getLetraUnidad());

                    Process p;
                    p = pb.start();

                    p.waitFor();
                    if (p.exitValue() != 0) {
                        System.out.println("Error! " + p.exitValue());
                        LogDony.write(this.getClass().getName()+" - ERROR: "+p.exitValue());
                        Util.setEstadoErrorCopia(keyDescarga);
                    } else {
                        Util.setEstadoCompletoCopia(keyDescarga, 0, 0);
                    }

                } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                    LogDony.write(this.getClass().getName()+" - ERROR: "+e);
                    Util.setEstadoErrorCopia(keyDescarga);
                }
            }

        };
        hiloCopiaArchivos.start();

        return Util.setEstadoCopiando(keyDescarga, 0, 0, 0);
        return null;
    }*/


    /*
    @Override
    public void olvidar(String ipModulo, String dniPersona, String nUnico, int nIncidente, String keyEleccion,
            String[] fechas) {
                String keyDescarga = Util.generarKeyDescarga(Util.getCurrentDate(), ipModulo, dniPersona,
                nUnico, nIncidente, keyEleccion, fechas);       

        Memory.remove(keyDescarga);
    }*/
}

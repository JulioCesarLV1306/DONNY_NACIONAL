package com.ncpp.asistenteexpedientes.sij.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import com.ncpp.asistenteexpedientes.sij.entity.Deposito;
import com.ncpp.asistenteexpedientes.sij.service.specs.DepositoService;
import com.ncpp.asistenteexpedientes.util.Constants;
import com.ncpp.asistenteexpedientes.util.DepositoWS;
import com.ncpp.asistenteexpedientes.util.Util;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class DepositoServiceImpl implements DepositoService {
    /*
     * public static String sql_fromWhere =
     * " FROM dba.escrito es, dba.expediente ex "
     * + " WHERE es.n_unico = ex.n_unico and es.n_incidente = ex.n_incidente "
     * + " AND ex.n_unico = ? AND ex.n_incidente = ? "
     * + " AND es.x_desc_depositoj like 'N%' ";
     */

    @Override
    public boolean generarReporte(String keyDescarga, String rutaDescarga, String nUnico, int nIncidente, String formatoExpediente) {

        try {
            DepositoWS depositoWS = new DepositoWS();
            List<Deposito> depositos = depositoWS.buscarPorExpediente(nUnico, nIncidente);

            for(Deposito dep : depositos){
               
                System.out.println( dep.toString());
            }

            String filenameJasper = Constants.RUTA_ARCHIVOS_CONF +"\\jasper\\depositos.jasper";
            String filenamePDF = Util.crearDirectorio(rutaDescarga) + "\\" + "reporte-depositos.pdf";

            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("expediente", formatoExpediente);
            File jasper = new File(filenameJasper);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters,
            new JRBeanCollectionDataSource(depositos));
            final File tempFile = new File(filenamePDF);
            JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(tempFile));


            /*JasperDesign jasperDesign = JRXmlLoader.load(filenameJrxml);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            JasperPrint jasperPrint = (JasperPrint) JasperFillManager.fillReport(jasperReport, hashMap,
                    new JRBeanCollectionDataSource(depositos));
            JasperExportManager.exportReportToPdfFile(jasperPrint, filenamePDF);*/

            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public int getConteo(String nUnico, int nIncidente) {
        int conteo = 0;
        try {
            DepositoWS depositoWS = new DepositoWS();
            conteo = depositoWS.buscarPorExpediente(nUnico, nIncidente).size();
        } catch (Exception e) {
            conteo = 0;
            System.out.println(e);
        }
        return conteo;
    }
}

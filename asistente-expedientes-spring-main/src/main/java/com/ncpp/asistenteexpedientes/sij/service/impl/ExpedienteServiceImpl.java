package com.ncpp.asistenteexpedientes.sij.service.impl;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import com.ncpp.asistenteexpedientes.sij.database.AccesoSij;
import com.ncpp.asistenteexpedientes.sij.entity.Expediente;
import com.ncpp.asistenteexpedientes.sij.payload.request.RequestBusquedaExpediente;
import com.ncpp.asistenteexpedientes.sij.payload.response.ResponseEleccionModel;
import com.ncpp.asistenteexpedientes.sij.service.specs.ExpedienteService;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ExpedienteServiceImpl implements ExpedienteService {

    /*
     * 
     * 1 => Administrador -> TODO
     * 2 => Fiscal Provincial PE -> TODO
     * 3 => Fiscal Adjunto Penal -> TODO
     * 4 => Asistente de Fiscal -> TODO
     * 5 => Defensor Publico -> TODO
     * 6 => Procaduria -> TODO
     * 7 => Abogado -> Apersonado en PENAL, otra especialidad TODO
     * 8 => Parte del Proceso -> Parte
     * 9 => Invitado -> NO TIENE ACCESO
     * 10 => Asistente Fiscal - ELIMINADO
     * 11 => CEM -> SOLO ACCESO A FAMILIA
     * 
     */

    private final static int TIPO_ADMINISTRADOR = 1;
    private final static int TIPO_FISCAL_PROVINCIAL = 2;
    private final static int TIPO_FISCAL_ADJUNTO = 3;
    private final static int TIPO_ASISTENTE_FISCAL = 4;
    private final static int TIPO_DEFENSOR_PUBLICO = 5;
    private final static int TIPO_PROCADURIA = 6;
    private final static int TIPO_ABOGADO = 7;
    //private final static int TIPO_PARTE_PROCESO = 8;
    private final static int TIPO_CEM = 11;

    @Override
    public Page<Expediente> buscar(RequestBusquedaExpediente request, Pageable page) {
        Connection cn = null;
        List<Expediente> lista = new ArrayList<>();
        int count_rows=0;
        try {
            cn = AccesoSij.getConnection();

            String sql_fromWhereLIBRE="FROM dba.expediente e " 
            +"INNER JOIN dba.instancia_expediente s ON s.n_unico = e.n_unico AND s.n_incidente = e.n_incidente AND s.l_ultimo = 'S' "
            +"INNER JOIN dba.instancia i ON s.c_instancia = i.c_instancia "
            +"INNER JOIN dba.expediente_ubicacion eu ON eu.n_incidente = e.n_incidente AND eu.n_unico = e.n_unico AND eu.l_ultimo = 'S' "
            +"INNER JOIN dba.ubicacion_expediente ue ON eu.c_ubicacion = ue.c_ubicacion "
            +"INNER JOIN dba.asignado_a asa on e.n_unico = asa.n_unico and e.n_incidente = asa.n_incidente "
            +"INNER JOIN dba.usuario u on u.c_usuario = asa.c_usuario and asa.l_ultimo = 'S' "
            +"WHERE e.c_cod_visualiza = 'S' AND (e.c_cod_medida_cautelar IS NULL OR e.c_incidente = '458') "
            +"AND e.x_formato like ? ";

            String sql_fromWhereABGPARTE="FROM dba.expediente e "
            +"INNER JOIN dba.instancia_expediente s ON s.n_unico = e.n_unico AND s.n_incidente = e.n_incidente AND s.l_ultimo = 'S' "
            +"INNER JOIN dba.instancia i ON s.c_instancia = i.c_instancia "
            +"INNER JOIN dba.expediente_ubicacion eu ON eu.n_incidente = e.n_incidente AND eu.n_unico = e.n_unico AND eu.l_ultimo = 'S' "
            +"INNER JOIN dba.ubicacion_expediente ue ON eu.c_ubicacion = ue.c_ubicacion "
            +"INNER JOIN dba.asignado_a asa on e.n_unico = asa.n_unico and e.n_incidente = asa.n_incidente "
            +"INNER JOIN dba.usuario u on u.c_usuario = asa.c_usuario and asa.l_ultimo = 'S' "
            +"INNER JOIN dba.parte p ON  p.n_unico = e.n_unico and p.n_incidente = e.n_incidente AND p.x_doc_id = '"+request.getDni()+"' AND p.l_tipo_parte = 'ABG' "
            +"WHERE e.c_cod_visualiza = 'S' AND (e.c_cod_medida_cautelar IS NULL OR e.c_incidente = '458') AND e.x_formato like ? ";

            String sql_fromWhereABGPARTEYLIBRE = sql_fromWhereLIBRE + " AND e.c_especialidad NOT IN  ('PE','ED') "
            + "UNION SELECT e.n_unico as n_unico, e.n_incidente as n_incidente,e.x_formato as formato_expediente, e.x_sumilla as sumilla, "
            + "i.x_nom_instancia as instancia_actual, ue.x_desc_ubicacion as ubicacion_actual, s.f_ingreso as fecha_ingreso,  u.x_nom_usuario as especialista_legal "
            + sql_fromWhereABGPARTE + " AND e.c_especialidad IN  ('PE','ED') " ;
                    
            String sql_fromWhereCEM= sql_fromWhereLIBRE + " AND e.c_especialidad IN  ('FA','FC','FP','FT') ";
           

            String sql_fromWhere="ERROR";
            int id_tipo=request.getIdTipo();

            //FILTRAR WHERE POR TIPO PARTE
            if(id_tipo==TIPO_ADMINISTRADOR 
            || id_tipo==TIPO_FISCAL_PROVINCIAL 
            || id_tipo==TIPO_FISCAL_ADJUNTO 
            || id_tipo==TIPO_ASISTENTE_FISCAL 
            || id_tipo==TIPO_DEFENSOR_PUBLICO 
            || id_tipo==TIPO_PROCADURIA){
                sql_fromWhere=sql_fromWhereLIBRE;
            } else if (id_tipo==TIPO_ABOGADO ){
                if(request.getEspecialidad().equals("PE") || request.getEspecialidad().equals("ED")){
                    sql_fromWhere=sql_fromWhereABGPARTE;
                } else if(request.getEspecialidad().equals("TODOS")){
                    sql_fromWhere=sql_fromWhereABGPARTEYLIBRE; 
                } else {
                    sql_fromWhere=sql_fromWhereLIBRE;
                }
            } else if(id_tipo==TIPO_CEM){
                sql_fromWhere=sql_fromWhereCEM;
            }
            
            //AGREGAR FILTRO ESPECIALIDAD A WHERE
            if(!request.getEspecialidad().equals("TODOS") && id_tipo!= TIPO_CEM ){
                if(request.getEspecialidad().equals("FA")){
                    sql_fromWhere=sql_fromWhere
                    +" AND ( e.c_especialidad IN ('FA')  OR e.c_especialidad IN ('FC') OR e.c_especialidad IN ('FP')  OR e.c_especialidad IN ('FT') ) ";
                }else{
                    sql_fromWhere=sql_fromWhere+" AND e.c_especialidad IN ('"+request.getEspecialidad()+"') ";
                }
            }

            String strCuaderno = request.getCuaderno() == -1 ? "" : ("-" + request.getCuaderno() + "-");
            String strNumero = Util.completeNumeroExpediente(request.getNumero());

            String like_clause = strNumero + "-" + request.getAnio() + strCuaderno + "%";

            long sql_rowcount=page.getPageSize()+page.getOffset();
            String sql = "SET ROWCOUNT "+sql_rowcount+" SELECT e.n_unico as n_unico, e.n_incidente as n_incidente,e.x_formato as formato_expediente, e.x_sumilla as sumilla, "
                    + "i.x_nom_instancia as instancia_actual, ue.x_desc_ubicacion as ubicacion_actual, s.f_ingreso as fecha_ingreso, u.x_nom_usuario as especialista_legal "
                    + sql_fromWhere;
                
            if(id_tipo == TIPO_ABOGADO && request.getEspecialidad().equals("TODOS") ){
                int countLibres = Util.countRows(sql_fromWhereLIBRE + " AND e.c_especialidad NOT IN  ('PE','ED') ", new Object[]{like_clause});
                int countAbgPart = Util.countRows(sql_fromWhereABGPARTE + " AND e.c_especialidad IN  ('PE','ED') ", new Object[]{like_clause});
                count_rows = countLibres + countAbgPart;
            }else{
                sql = sql + " ORDER BY s.f_ingreso desc ";
                count_rows = Util.countRows(sql_fromWhere, new Object[]{like_clause});
            }
            /*System.out.println("like_clause: "+like_clause);
            System.out.println("SQL: "+sql);
            System.out.println("sql_rowcount: "+sql_rowcount);
            System.out.println("count_rows: "+count_rows);*/
                            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, like_clause);
            if (id_tipo ==TIPO_ABOGADO && request.getEspecialidad().equals("TODOS")){
                pstm.setString(2, like_clause);
            }

            /*pstm.setInt(2, page.getPageSize());
            pstm.setLong(3, page.getOffset());*/
            ResultSet rs = pstm.executeQuery();

            int number_row = 0;
            while (rs.next()) {
                number_row++;
                if (number_row > page.getOffset()) {
                    lista.add(createExpediente(rs));
                }
            }
            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.out.println(e);
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            lista = null;
        } finally {
            try {
                cn.close();
            } catch (Exception e) {
                System.out.println(e);
                LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }
        }
        return new PageImpl<Expediente>(lista, page, count_rows);
    }

    @Override
    public List<ResponseEleccionModel> getConteos(String nUnico, int nIncidente) {
        List<ResponseEleccionModel> listaResponse = new ArrayList<ResponseEleccionModel>();

        int conteoActas = Util.countRows(ActaServiceImpl.sql_fromWhereACTAS, new Object[] { nUnico, nIncidente });
        conteoActas = conteoActas + Util.countRows(ActaServiceImpl.sql_fromWhereRESOL, new Object[] { nUnico, nIncidente });
        int conteoVideos = Util.countRows(VideoServiceImpl.sql_fromWhere, new Object[] { nUnico, nIncidente });
        int conteoResoluciones = Util.countRows(ResolucionServiceImpl.sql_fromWhere,
                new Object[] { nUnico, nIncidente });
        int conteoDocumentosDigitalizados = Util.countRows(DocumentoDigitalizadoServiceImpl.sql_fromWhere,
                new Object[] { nUnico, nIncidente });
            DepositoServiceImpl depositoServiceImpl = new DepositoServiceImpl();
        int conteoDepositos= depositoServiceImpl.getConteo(nUnico, nIncidente);

        listaResponse.add(new ResponseEleccionModel("actas", conteoActas));
        listaResponse.add(new ResponseEleccionModel("videos", conteoVideos));
        listaResponse.add(new ResponseEleccionModel("resoluciones", conteoResoluciones));
        listaResponse.add(new ResponseEleccionModel("documentosdigitalizados", conteoDocumentosDigitalizados));
        listaResponse.add(new ResponseEleccionModel("depositos", conteoDepositos));

        return listaResponse;
    }

    private Expediente createExpediente(ResultSet rs) {
        Expediente expediente = new Expediente();
        try {
            expediente.setNUnico(rs.getString("n_unico"));
            expediente.setNIncidente(rs.getInt("n_incidente"));
            expediente.setFechaIngreso(rs.getDate("fecha_ingreso"));
            expediente.setFormatoExpediente(rs.getString("formato_expediente"));
            expediente.setInstanciaActual(rs.getString("instancia_actual"));
            expediente.setSumilla(rs.getString("sumilla"));
            expediente.setUbicacionActual(rs.getString("ubicacion_actual"));
            expediente.setEspecialistaLegal(rs.getString("especialista_legal"));

        } catch (SQLException e) {
            expediente = null;
            System.out.println(e);
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        }
        return expediente;
    }

    @Override
    public Page<Expediente> buscarPorDNI(String dni, String especialidad, Pageable page) {
        Connection cn = null;
        List<Expediente> lista = new ArrayList<>();
        int count_rows = 0;
        try {
            cn = AccesoSij.getConnection();

            String sql_fromWhereIMPUTAGRAV = "FROM dba.expediente e "
            +"INNER JOIN dba.instancia_expediente s ON s.n_unico = e.n_unico AND s.n_incidente = e.n_incidente AND s.l_ultimo = 'S' "
            +"INNER JOIN dba.instancia i ON s.c_instancia = i.c_instancia "
            +"INNER JOIN dba.expediente_ubicacion eu ON eu.n_incidente = e.n_incidente AND eu.n_unico = e.n_unico AND eu.l_ultimo = 'S' "
            +"INNER JOIN dba.ubicacion_expediente ue ON eu.c_ubicacion = ue.c_ubicacion "
            +"INNER JOIN dba.asignado_a asa on e.n_unico = asa.n_unico and e.n_incidente = asa.n_incidente "
            +"INNER JOIN dba.usuario u on u.c_usuario = asa.c_usuario and asa.l_ultimo = 'S' "
            +"INNER JOIN dba.parte p ON  p.n_unico = e.n_unico and p.n_incidente = e.n_incidente AND p.x_doc_id = ? AND  " + generateTipoParte()
            +"WHERE e.c_cod_visualiza = 'S' AND (e.c_cod_medida_cautelar IS NULL OR e.c_incidente = '458') ";

            String sql_fromWhere = "ERROR";

            if (!especialidad.equals("TODOS")) {
                if (especialidad.equals("FA")) {
                    sql_fromWhere = sql_fromWhereIMPUTAGRAV
                            + " AND ( e.c_especialidad IN ('FA')  OR e.c_especialidad IN ('FC') OR e.c_especialidad IN ('FP')  OR e.c_especialidad IN ('FT') ) ";
                } else {
                    sql_fromWhere = sql_fromWhereIMPUTAGRAV + " AND e.c_especialidad IN ('" + especialidad + "')";
                }
            } else {
                sql_fromWhere = sql_fromWhereIMPUTAGRAV;
            }

            long sql_rowcount = page.getPageSize() + page.getOffset();
            String sql = "SET ROWCOUNT " + sql_rowcount
                    + " SELECT e.n_unico as n_unico, e.n_incidente as n_incidente,e.x_formato as formato_expediente, e.x_sumilla as sumilla, "
                    + "i.x_nom_instancia as instancia_actual, ue.x_desc_ubicacion as ubicacion_actual, s.f_ingreso as fecha_ingreso, u.x_nom_usuario as especialista_legal "
                    + sql_fromWhere + " ORDER BY s.f_ingreso desc ";

            // System.out.println("SQL BY DNI: "+sql);

            count_rows = Util.countRows(sql_fromWhere, new Object[] { dni });

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, dni);
            /*
             * pstm.setInt(2, page.getPageSize());
             * pstm.setLong(3, page.getOffset());
             */
            ResultSet rs = pstm.executeQuery();

            int number_row = 0;
            while (rs.next()) {
                number_row++;
                if (number_row > page.getOffset()) {
                    lista.add(createExpediente(rs));
                }
            }
            rs.close();
            pstm.close();
        } catch (Exception e) {
            System.out.println(e);
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            lista = null;
        } finally {
            try {
                cn.close();
            } catch (Exception e) {
                System.out.println(e);
                LogDony.write(this.getClass().getName() + " - ERROR: " + e);
            }
        }
        return new PageImpl<Expediente>(lista, page, count_rows);
    }

    private String generateTipoParte() {
        List<String> listaTipoParte = Arrays.asList(
                "AGR", // AGRAVIADO
                "APD", // APODERADO
                "BEN", // BENEFICIARIO
                "CBR", // COLABORADOR
                "CRR", // CURADOR
                "DDO", // DEMANDADO
                "DTE", // DEMANDANTE
                "DNO", // DENUNCIADO
                "EMP", // EMPLAZADO
                "EMT", // EMPLAZANTE
                "EXT", // EXTRADITABLE
                "EX1", // EXTRADITADO
                "IMP", // IMPUTADO
                "INC", // INCULPADO
                "INT", // INVERVINIENTE
                "QDO", // QUEJADO
                "QSO", // QUEJOSO
                "QRD", // QUERELLADO
                "QRT", // QUERELLANTE
                "RPR", // REPRESENTANTE
                "REQ", // REQUERIDO
                "RET", // REQUIRIENTE
                "RQT", // REQUISITORIADO
                "SLT", // SOLICITADO
                "SOL", // SOLICITANTE
                "TES"); // TESTIGO

        String concatString = " ( ";
        for (String tipoParte : listaTipoParte) {
            if (!concatString.equals(" ( ")) {
                concatString += " OR ";
            }
            concatString += " p.l_tipo_parte = '" + tipoParte + "' ";
        }
        concatString += " ) ";
        return concatString;
    }

}

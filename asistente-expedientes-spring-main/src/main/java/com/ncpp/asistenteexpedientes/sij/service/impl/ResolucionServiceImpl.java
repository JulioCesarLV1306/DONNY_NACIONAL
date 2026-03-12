package com.ncpp.asistenteexpedientes.sij.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ncpp.asistenteexpedientes.sij.database.AccesoSij;
import com.ncpp.asistenteexpedientes.sij.entity.Archivo;
import com.ncpp.asistenteexpedientes.sij.entity.Fecha;
import com.ncpp.asistenteexpedientes.sij.entity.ServidorFtp;
import com.ncpp.asistenteexpedientes.sij.service.specs.ResolucionService;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ResolucionServiceImpl implements ResolucionService{

    public static String sql_fromWhere = " FROM DBA.resolucion_editor re, DBA.expediente e, DBA.acto_procesal ap, " +
    "DBA.instancia i,  " +
    "DBA.instancia_expediente s, DBA.servidor_ftp ftp " +
    "WHERE re.n_unico = e.n_unico AND re.n_incidente = e.n_incidente " +
    "AND ap.c_acto_procesal = re.c_acto_procesal AND re.l_utilizado != 'A' " +
    "AND i.c_instancia = re.c_instancia " +
    "AND s.n_unico = e.n_unico AND s.n_incidente = e.n_incidente " +
    "AND s.c_instancia = i.c_instancia  AND e.c_cod_medida_cautelar IS NULL " +
    "AND e.c_cod_visualiza = 'S' " +
    "AND re.n_item = ftp.n_item " +
    "AND re.n_correlativo_ftp = ftp.n_correlativo_ftp " +
    "AND ftp.l_activo = 'S' AND re.l_ind_utilizado = 'S' " +
    "AND re.l_ind_pdf = 'S' AND ap.c_acto_procesal != '06C' "+ //SE AGREGO PARA QUITAR ACTAS NUEVAS
    "AND e.n_unico = ? AND e.n_incidente = ? ";


    @Override
    public List<Archivo> buscarTodo(String nUnico, int nIncidente) {
     return buscarPorFechas(nUnico, nIncidente, null);
    }
     
    @Override
    public List<Archivo> buscarPorFechas(String nUnico, int nIncidente, String[] fechas) {
        Connection cn = null;
        List<Archivo> lista = new ArrayList<>();
        try {
            cn = AccesoSij.getConnection();
            String sql = "select DATEFORMAT(re.f_descargo, 'YYYY-MM-DD') as fecha, re.x_ruta_ftp as ruta_ftp, re.x_nombre_doc as nombre_doc, re.x_nombre_pdf as nombre_pdf, "
            + "ftp.c_usuario as ftp_usuario, ftp.c_clave as ftp_clave, ftp.x_ip as ftp_ip "
            + sql_fromWhere + " and "+Util.completeWhereSqlFechas("re.f_descargo", fechas);

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                lista.add(createResolucion(rs));
            }
            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.out.println(e);
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            lista = null;
        } finally{
            try {
               cn.close();
            } catch (Exception e) {
               System.out.println(e);
               LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }
        }
        //System.out.println(lista.toString());
        return lista.size() == 0 ? null : lista;
    }


    @Override
    public Page<Fecha> getFechas(String nUnico, int nIncidente, Pageable page) {
        Connection cn = null;
        List<Fecha> lista = new ArrayList<>();
        int count_rows=0;
        try {
            cn = AccesoSij.getConnection();
            String sql = "select DATEFORMAT(re.f_descargo, 'YYYY-MM-DD') as fecha, re.x_sumilla as sumilla "
                        + sql_fromWhere + " order by DATEFORMAT(re.f_descargo, 'YYYY-MM-DD') desc ";

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            ResultSet rs = pstm.executeQuery();
            count_rows = Util.countRowsFechas(sql_fromWhere,"re.f_descargo",new Object[]{nUnico,nIncidente});
            lista = Util.getListaFechas(rs, page);
            

            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.out.println(e);
            lista = null;
        } finally{
            try {
               cn.close();
            } catch (Exception e) {
               System.out.println(e);
               LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }
        }
        return new PageImpl<Fecha>(lista.size() == 0 ? null : lista, page, count_rows);
   
    }

    private Archivo createResolucion(ResultSet rs){
        ServidorFtp servidorFtp = new ServidorFtp();
        Archivo resolucion = new Archivo();
        try {
            servidorFtp.setNombreUsuario(rs.getString("ftp_usuario"));
            servidorFtp.setClaveUsuario(rs.getString("ftp_clave"));
            servidorFtp.setIpServer(rs.getString("ftp_ip"));

            resolucion.setServidorFtp(servidorFtp);
            resolucion.setFecha(rs.getString("fecha"));
            resolucion.setRuta(rs.getString("ruta_ftp"));
            String nombre_pdf = rs.getString("nombre_pdf");
            resolucion.setNombre(nombre_pdf == null ? rs.getString("nombre_doc")+".doc":nombre_pdf);
        } catch (Exception e) {
            resolucion = null;
            System.out.println(e);
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
        }
        return resolucion;
    }

    
    
}

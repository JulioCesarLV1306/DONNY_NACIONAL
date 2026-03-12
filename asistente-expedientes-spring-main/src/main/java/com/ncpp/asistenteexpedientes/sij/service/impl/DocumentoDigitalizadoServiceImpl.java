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
import com.ncpp.asistenteexpedientes.sij.service.specs.DocumentoDigitalizadoService;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class DocumentoDigitalizadoServiceImpl implements DocumentoDigitalizadoService{

    public static String sql_fromWhere=" FROM DBA.documento_digital_escrito dde " +
                                "JOIN DBA.documento_digital dd ON dde.n_documento = dd.n_documento AND dde.c_sede = dd.c_sede " +
                                "LEFT JOIN DBA.servidor_ftp ftp ON  ftp.c_sede = dd.c_sede_ftp AND ftp.n_correlativo_ftp = dd.n_correlativo_ftp " +
                                "LEFT JOIN DBA.escrito s ON s.n_sec_digitalizacion=dde.n_sec_ingreso AND s.n_ano_ingreso=dde.n_ano_ingreso " +
                                "WHERE dde.n_unico = ? AND dde.n_incidente = ? AND (s.x_sumilla not like '%LEVANTAMIENTO%' OR s.x_sumilla IS NULL) ";


    @Override
    public List<Archivo> buscarTodo(String nUnico, int nIncidente) {
        return buscarPorFechas(nUnico,nIncidente, null);
    }

    @Override
    public List<Archivo> buscarPorFechas(String nUnico, int nIncidente, String[] fechas) {
        Connection cn = null;
        List<Archivo> lista = new ArrayList<>();
        try {
            cn = AccesoSij.getConnection();
            String sql = "SELECT DATEFORMAT(dde.f_asociacion, 'YYYY-MM-DD') as fecha, dd.x_ruta_archivo as ruta_archivo, dd.x_nombre_archivo as nombre_archivo, "
            + "ftp.c_usuario as ftp_usuario, ftp.c_clave as ftp_clave, ftp.x_ip as ftp_ip " + sql_fromWhere
            +" and " +Util.completeWhereSqlFechas("dde.f_asociacion", fechas);

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                lista.add(createDocumentoDigitalizado(rs));
            }
            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            lista = null;
        } finally{
            try {
               cn.close();
            } catch (Exception e) {
               System.out.println(e);
               e.printStackTrace();
               LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }
        }
        return lista.size() == 0 ? null : lista;
    }

    @Override
    public Page<Fecha> getFechas(String nUnico, int nIncidente, Pageable page) {
        Connection cn = null;
        List<Fecha> lista = new ArrayList<>();
        int count_rows=0;
        try {
            cn = AccesoSij.getConnection();

            String sql = "select DATEFORMAT(dde.f_asociacion, 'YYYY-MM-DD') as fecha, dd.x_descripcion as sumilla "
                        + sql_fromWhere + " order by DATEFORMAT(dde.f_asociacion, 'YYYY-MM-DD') desc ";

            

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            ResultSet rs = pstm.executeQuery();

            count_rows = Util.countRowsFechas(sql_fromWhere,"dde.f_asociacion",new Object[]{nUnico,nIncidente});
            lista = Util.getListaFechas(rs, page);
            
            rs.close();
            pstm.close();
            
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            lista = null;
        } finally{
            try {
               cn.close();
            } catch (Exception e) {
               System.out.println(e);
               e.printStackTrace();
               LogDony.write(this.getClass().getName()+" - ERROR: "+e);
            }
        }
        return new PageImpl<Fecha>(lista.size() == 0 ? null : lista, page, count_rows);
    }


    private Archivo createDocumentoDigitalizado(ResultSet rs){
        ServidorFtp servidorFtp = new ServidorFtp();
        Archivo digitalizado = new Archivo();
        try {
            servidorFtp.setNombreUsuario(rs.getString("ftp_usuario"));
            servidorFtp.setClaveUsuario(rs.getString("ftp_clave"));
            servidorFtp.setIpServer(rs.getString("ftp_ip"));

            digitalizado.setServidorFtp(servidorFtp);
            digitalizado.setRuta(rs.getString("ruta_archivo"));
            digitalizado.setNombre(rs.getString("nombre_archivo"));
            digitalizado.setFecha(rs.getString("fecha"));
        } catch (Exception e) {
            digitalizado = null;
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
        }
        return digitalizado;
    }
    
}

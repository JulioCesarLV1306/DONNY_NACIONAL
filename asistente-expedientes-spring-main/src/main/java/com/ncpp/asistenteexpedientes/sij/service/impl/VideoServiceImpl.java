package com.ncpp.asistenteexpedientes.sij.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ncpp.asistenteexpedientes.sij.database.AccesoSij;
import com.ncpp.asistenteexpedientes.sij.entity.Archivo;
import com.ncpp.asistenteexpedientes.sij.entity.Fecha;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.ncpp.asistenteexpedientes.sij.entity.ServidorFtp;
import com.ncpp.asistenteexpedientes.sij.service.specs.VideoService;

public class VideoServiceImpl implements VideoService {

    public static String sql_fromWhere = "FROM  DBA.audiencia_programacion ap "
    +"inner join dba.audiencia_video av on ap.n_sala = av.n_sala and ap.c_audiencia = av.c_audiencia and ap.n_programacion = av.n_programacion "
    +"inner join dba.servidor_ftp ftp on ftp.c_sede = av.c_sede_ftp and  ftp.n_correlativo_ftp = av.n_correlativo_ftp and ftp.n_item = av.n_item "
    +"inner join dba.audiencia a on ap.c_audiencia = a.c_audiencia and ap.n_programacion = a.n_programacion and av.n_audiencia = a.n_audiencia and a.l_activo = 'S' "
    +"where ap.n_unico = ? and ap.n_incidente = ? and ap.l_estado = 'REAL' and ap.l_activo = 'S' ";

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
            String sql = "select DATEFORMAT(ap.f_audiencia, 'YYYY-MM-DD') as fecha, av.x_filename as name_video, "
                    + "ftp.c_usuario as ftp_usuario, ftp.c_clave as ftp_clave, ftp.x_ip as ftp_ip, a.x_file_acta as name_acta, a.n_audiencia as n_audiencia "
                    + sql_fromWhere + " and " + Util.completeWhereSqlFechas("ap.f_audiencia", fechas);

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                lista.add(createVideo(rs));
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
        return lista.size() == 0 ? null : lista;
    }

    @Override
    public Page<Fecha> getFechas(String nUnico, int nIncidente, Pageable page) {
        Connection cn = null;
        List<Fecha> lista = new ArrayList<>();
        int count_rows=0;
        try {
            cn = AccesoSij.getConnection();

            String sql = "select DATEFORMAT(ap.f_audiencia, 'YYYY-MM-DD') as fecha, a.x_desc_audiencia as sumilla "
                        + sql_fromWhere + " order by DATEFORMAT(ap.f_audiencia, 'YYYY-MM-DD') desc ";

            

            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            ResultSet rs = pstm.executeQuery();

            count_rows = Util.countRowsFechas(sql_fromWhere,"ap.f_audiencia",new Object[]{nUnico,nIncidente});
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

    private Archivo createVideo(ResultSet rs) {
        ServidorFtp servidorFtp = new ServidorFtp();
        Archivo video = new Archivo();
        try {
            servidorFtp.setNombreUsuario(rs.getString("ftp_usuario"));
            servidorFtp.setClaveUsuario(rs.getString("ftp_clave"));
            servidorFtp.setIpServer(rs.getString("ftp_ip"));

            video.setServidorFtp(servidorFtp);
            video.setNombre(rs.getString("name_video"));
            video.setFecha(rs.getString("fecha"));
            String name_acta = rs.getString("name_acta");
            String n_audiencia = rs.getString("n_audiencia");
            video.setRuta(Util.getRutaByNombreActa(name_acta, n_audiencia));

        } catch (Exception e) {
            video = null;
            System.out.println(e);
            LogDony.write(this.getClass().getName() + " - ERROR: " + e);
        }
        return video;
    }

}

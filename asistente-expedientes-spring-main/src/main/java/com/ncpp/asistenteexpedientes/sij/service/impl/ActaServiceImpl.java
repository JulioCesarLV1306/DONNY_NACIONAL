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
import com.ncpp.asistenteexpedientes.sij.service.specs.ActaService;
import com.ncpp.asistenteexpedientes.util.LogDony;
import com.ncpp.asistenteexpedientes.util.Util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ActaServiceImpl implements ActaService{

    /*public static String sql_fromWhere =" FROM DBA.audiencia a, DBA.historia_audiencia ha , DBA.expediente e, "
    +"DBA.instancia i, DBA.instancia_expediente s, DBA.acto_procesal ap, "
    +"DBA.servidor_ftp ftp "
    +"WHERE ha.n_audiencia = a.n_audiencia AND ha.n_sala = a.n_sala "
    +"AND ha.c_audiencia = a.c_audiencia AND e.n_unico = ha.n_unico "
    +"AND e.n_incidente = ha.n_incidente "
    +"AND i.c_instancia = ha.c_instancia "
    +"AND ap.c_acto_procesal = ha.c_acto_procesal "
    +"AND s.n_unico = e.n_unico AND s.n_incidente = e.n_incidente "
    +"AND s.c_instancia = i.c_instancia "
    +"AND e.c_cod_medida_cautelar IS NULL AND e.c_cod_visualiza = 'S' "
    +"AND ftp.c_sede = a.c_sede_ftp and  ftp.n_item = a.n_item AND "
    +"e.n_unico = ? AND e.n_incidente = ? ";*/

    public static String sql_fromWhereACTAS = "FROM  DBA.audiencia_programacion ah "
    +"INNER JOIN DBA.audiencia a ON ah.n_sala = a.n_sala AND ah.c_audiencia = a.c_audiencia "
    +"AND ah.n_programacion = a.n_programacion  AND a.l_activo = 'S' AND a.f_resolucionEditor IS NULL "
    +"LEFT JOIN DBA.audiencia_estado ae on a.c_situacion = ae.c_estado "
    +"WHERE ah.n_unico = ? AND ah.n_incidente = ? AND ah.l_activo = 'S' ";

    public static String sql_fromWhereRESOL =
    "FROM DBA.historia h "
    +"WHERE h.n_unico = ? AND h.n_incidente = ? "
    +"AND h.l_visualizacion = 'S' AND h.c_acto_procesal = '06C' AND h.f_resolucion_editor IS NOT NULL AND h.l_visualizacion = 'S' ";

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
            String sql = 
            "SELECT a.n_audiencia as n_audiencia , "
            +"DATEFORMAT(a.f_creacion, 'YYYY-MM-DD') as fecha,  "
            +"'-' as ruta_ftp, a.x_file_acta as nombre_doc,  "
            +"ftp.c_usuario as ftp_usuario, ftp.c_clave as ftp_clave, ftp.x_ip as ftp_ip "
            +"FROM  DBA.audiencia_programacion ah "
            +"INNER JOIN DBA.audiencia a ON ah.n_sala = a.n_sala AND ah.c_audiencia = a.c_audiencia  "
            +"AND ah.n_programacion = a.n_programacion  AND a.l_activo = 'S' AND a.f_resolucionEditor IS NULL "
            +"LEFT JOIN DBA.audiencia_estado ae on a.c_situacion = ae.c_estado "
            +"INNER JOIN DBA.servidor_ftp ftp ON ftp.c_sede = a.c_sede_ftp AND ftp.n_item = a.n_item "
            +"WHERE ah.n_unico = ? AND ah.n_incidente = ? AND ah.l_activo = 'S'  "
            +"and " + Util.completeWhereSqlFechas("fecha", fechas)
            +"UNION SELECT  "
            +"0 as n_audiencia, "
            +"DATEFORMAT(h.f_ingreso_acto, 'YYYY-MM-DD') as fecha,  "
            +"re.x_ruta_ftp as ruta_ftp, re.x_nombre_pdf as nombre_doc,  "
            +"ftp.c_usuario as ftp_usuario , ftp.c_clave as ftp_clave , ftp.x_ip as ftp_ip  "
            +"FROM DBA.historia h "
            +"inner join dba.resolucion_editor re on re.n_unico = h.n_unico and re.n_incidente = h.n_incidente and h.c_acto_procesal = re.c_acto_procesal and h.f_resolucion_editor = re.f_descargo "
            +"INNER JOIN DBA.servidor_ftp ftp ON ftp.c_sede = re.c_sede_ftp AND ftp.n_item = re.n_item "
            +"WHERE h.n_unico = ? AND h.n_incidente = ? "
            +"AND h.l_visualizacion = 'S' AND h.c_acto_procesal = '06C' AND h.f_resolucion_editor IS NOT NULL AND h.l_visualizacion = 'S' "
            +"and " + Util.completeWhereSqlFechas("fecha", fechas);
       
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            pstm.setString(3, nUnico);
            pstm.setInt(4, nIncidente);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                lista.add(createActa(rs));
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

            String sql = 
            "SELECT  DATEFORMAT(a.f_creacion, 'YYYY-MM-DD') as fecha, "
            +"case when ae.x_desc_estado is null then a.x_desc_audiencia else a.x_desc_audiencia+ ' :' +  ae.x_desc_estado  end as sumilla "
            +"FROM  DBA.audiencia_programacion ah "
            +"INNER JOIN DBA.audiencia a ON ah.n_sala = a.n_sala AND ah.c_audiencia = a.c_audiencia  "
            +"AND ah.n_programacion = a.n_programacion  AND a.l_activo = 'S' AND a.f_resolucionEditor IS NULL "
            +"LEFT JOIN DBA.audiencia_estado ae on a.c_situacion = ae.c_estado "
            +"WHERE ah.n_unico = ? AND ah.n_incidente = ? AND ah.l_activo = 'S'  "
            +"UNION SELECT  "
            +"DATEFORMAT(h.f_ingreso_acto, 'YYYY-MM-DD') as fecha,  "
            +"h.x_resolucion as sumilla "
            +"FROM DBA.historia h "
            +"WHERE h.n_unico = ? AND h.n_incidente = ?  "
            +"AND h.l_visualizacion = 'S' AND h.c_acto_procesal = '06C' AND h.f_resolucion_editor IS NOT NULL AND h.l_visualizacion = 'S' "
            +"order by fecha desc ";

            
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, nUnico);
            pstm.setInt(2, nIncidente);
            pstm.setString(3, nUnico);
            pstm.setInt(4, nIncidente);
            ResultSet rs = pstm.executeQuery();

            lista = Util.getListaFechas(rs, page);
            count_rows = Util.countRows(sql_fromWhereACTAS, new Object[]{nUnico,nIncidente});
            count_rows = count_rows + Util.countRows(sql_fromWhereRESOL, new Object[]{nUnico,nIncidente});

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

    private Archivo createActa(ResultSet rs){
        ServidorFtp servidorFtp = new ServidorFtp();
        Archivo acta = new Archivo();
        try {
            servidorFtp.setNombreUsuario(rs.getString("ftp_usuario"));
            servidorFtp.setClaveUsuario(rs.getString("ftp_clave"));
            servidorFtp.setIpServer(rs.getString("ftp_ip"));
            acta.setServidorFtp(servidorFtp);
            acta.setFecha(rs.getString("fecha"));
            String nombre_doc=rs.getString("nombre_doc");
            String n_audiencia = rs.getString("n_audiencia");
            acta.setNombre(nombre_doc);
            String ruta_ftp = rs.getString("ruta_ftp");
            if(ruta_ftp.equals("-")){
                acta.setRuta(Util.getRutaByNombreActa(nombre_doc,n_audiencia));
            }else{
                acta.setRuta(ruta_ftp);
            }
            
            
        } catch (Exception e) {
            acta = null;
            System.out.println(e);
            e.printStackTrace();
            LogDony.write(this.getClass().getName()+" - ERROR: "+e);
        }
        return acta;
    }

    
    
}

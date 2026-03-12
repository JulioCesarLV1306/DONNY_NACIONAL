package service;

import config.JPA;
import model.Modulo;
import javax.persistence.Query;

/**
 * Servicio para gestión de módulos (PCs autorizados)
 * 
 * Refactorizado V2.0 para nueva nomenclatura
 * 
 * @author JC
 * @version 2.0
 */
public class ModuloService extends JPA {

    public Modulo findModuloByIp(String localIp) {
        Modulo modulo = null;
        String sql = "SELECT m FROM Modulo m WHERE m.cPcIp = '" + localIp + "' AND m.nEstado = 1";

        try {
            Query query = getEntityManager().createQuery(sql);
            modulo = (Modulo) query.getSingleResult();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                closeEntityManager();
            } catch (Exception e2) {
                System.out.println(e2);
            }
        }
        return modulo;
    }

}

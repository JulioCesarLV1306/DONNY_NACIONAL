package service;

import config.JPA;
import javax.persistence.EntityTransaction;
import model.Bitacora;

public class BitacoraService extends JPA {

    public void create(Bitacora bitacora) {
        EntityTransaction t = null;
                
        try {
            t = getEntityManager().getTransaction();
            if (!t.isActive()) {
                t.begin();
            }
            getEntityManager().persist(bitacora);
            t.commit();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                closeEntityManager();
            } catch (Exception e2) {
                 System.out.println(e2);
            }
        }

    }
}

package config;

import javax.persistence.EntityManager;

public class JPA extends JPAFactory {
	
	public EntityManager getEntityManager() {
		return JPAFactory.createEntityManager();
	}
	
	public void closeEntityManager() {
		JPAFactory.close();
	}
}

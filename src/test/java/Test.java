
iimport hibernate.conventions.util.ConventionUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
public class Test {

	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");
		try {
			doSomething(entityManagerFactory);
		} finally {
			entityManagerFactory.close();
		}
	}

	private static void doSomething(EntityManagerFactory entityManagerFactory) {
		ConventionUtils.opendHSQLDBManager(entityManagerFactory);
	}

}

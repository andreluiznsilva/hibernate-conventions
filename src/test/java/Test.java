import hibernate.conventions.util.ConventionUtils;

import java.sql.Connection;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hsqldb.util.DatabaseManagerSwing;

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

		Connection connection = ConventionUtils.getConnection(entityManagerFactory);

		DatabaseManagerSwing manager = new DatabaseManagerSwing();
		manager.main();
		manager.connect(connection);
		manager.start();

	}

}

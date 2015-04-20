import hibernate.conventions.utils.ConventionUtils;

import java.sql.Connection;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hsqldb.util.DatabaseManagerSwing;

public class HSQL {

	public static void main(String[] args) {

		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sequence");

		try {

			Connection connection = ConventionUtils.getConnection(entityManagerFactory);

			DatabaseManagerSwing manager = new DatabaseManagerSwing();
			manager.main();
			manager.connect(connection);
			manager.start();

		} finally {
			entityManagerFactory.close();
		}

	}

}

package hibernate.conventions.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class TestUtils {

	private TestUtils() {
	}

	public static void assertSql(List<String> script, String... sqls) {
		assertEquals(sqls.length, script.size());
		for (int i = 0; i < sqls.length; i++) {
			assertEquals(sqls[i], script.get(i));
		}
	}

	public static void execute( String query, EntityManagerFactory entityManagerFactory) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery(query).executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.close();
	}

}

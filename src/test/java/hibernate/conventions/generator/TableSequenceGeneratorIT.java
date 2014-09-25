package hibernate.conventions.generator;

import static org.junit.Assert.*;
import hibernate.conventions.dummy.DummyEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TableSequenceGeneratorIT {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("test");
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testConfigureNoTableName() {

		EntityManager em = entityManagerFactory.createEntityManager();

		enableOracleSyntax(em);

		Number before = sequenceNextVal(em);
		DummyEntity dummy = new DummyEntity("test");

		em.getTransaction().begin();
		em.persist(dummy);
		em.getTransaction().commit();

		Number after = sequenceNextVal(em);

		assertEquals(1, before);
		assertEquals(2, dummy.getId().longValue());
		assertEquals(3, after);

	}

	private void enableOracleSyntax(EntityManager em) {
		em.getTransaction().begin();
		em.createNativeQuery("SET DATABASE SQL SYNTAX ORA TRUE").executeUpdate();
		em.getTransaction().commit();
	}

	private Number sequenceNextVal(EntityManager em) {
		return (Number) em.createNativeQuery("SELECT seq_DummyEntity.NEXTVAL FROM DUAL").getSingleResult();
	}

}

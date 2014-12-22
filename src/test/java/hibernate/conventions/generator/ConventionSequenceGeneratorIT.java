package hibernate.conventions.generator;

import static org.junit.Assert.assertEquals;
import hibernate.conventions.dummy.DummyEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConventionSequenceGeneratorIT {

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("test");
		entityManager = entityManagerFactory.createEntityManager();
	}

	@After
	public void tearDown() throws Exception {
		entityManager.close();
		entityManagerFactory.close();
	}

	@Test
	public void testSequenceGenerator() {

		Number before = sequenceNextVal();
		DummyEntity dummy = new DummyEntity("test");

		entityManager.getTransaction().begin();
		entityManager.persist(dummy);
		entityManager.getTransaction().commit();

		Number after = sequenceNextVal();

		assertEquals(1, before);
		assertEquals(2, dummy.getId().longValue());
		assertEquals(3, after);

	}

	private Number sequenceNextVal() {
		return (Number) entityManager.createNativeQuery("call NEXT VALUE FOR seqDummyEntity").getSingleResult();
	}

}

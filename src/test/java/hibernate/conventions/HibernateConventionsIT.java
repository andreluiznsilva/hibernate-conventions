package hibernate.conventions;

import static org.junit.Assert.*;
import hibernate.conventions.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateConventionsIT {

	private EntityManagerFactory entityManagerFactory;
	private HibernateConventions conventions;
	private Configuration configuration;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("test");
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		ServiceRegistry serviceRegistry = (ServiceRegistry) ReflectionUtils.getFieldValue("serviceRegistry",
				sessionFactory);
		configuration = (Configuration) ReflectionUtils.getFieldValue("configuration", serviceRegistry);
		conventions = new HibernateConventions(configuration);
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testNormalize() {
		conventions.normalize();
	}

	@Test
	public void testValidate() {
		conventions.validate();
	}

	@Test(expected = RuntimeException.class)
	public void testValidateMaxLength() {
		configuration.setProperty("hibernate.conventions.maxLength", "5");
		conventions.validate();
	}

}

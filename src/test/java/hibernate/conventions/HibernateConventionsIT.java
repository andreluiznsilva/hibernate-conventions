package hibernate.conventions;

import static org.junit.Assert.assertTrue;
import hibernate.conventions.dummy.TestConventionNamingStrategy;
import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;
import hibernate.conventions.util.ReflectionUtils;

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
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testNormalize() {
		conventions = new HibernateConventions(configuration);
		conventions.normalize();
	}

	@Test
	public void testValidate() {
		conventions = new HibernateConventions(configuration);
		conventions.validate();
	}

	@Test(expected = RuntimeException.class)
	public void testValidateMaxLength() {

		configuration.setProperty("hibernate.conventions.maxLength", "5");

		conventions = new HibernateConventions(configuration);
		conventions.validate();

	}

	@Test
	public void testEmptyNamingStrategy() {

		configuration.setNamingStrategy(null);

		conventions = new HibernateConventions(configuration);
		ConventionNamingStrategy strategy = conventions.getStrategy();

		assertTrue(strategy instanceof DefaultConventionNamingStrategy);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConventionNamingStrategy() {

		configuration.setNamingStrategy(new org.hibernate.cfg.DefaultNamingStrategy());

		conventions = new HibernateConventions(configuration);

	}

	@Test
	public void testConfiguredConventionNamingStrategy() {

		conventions = new HibernateConventions(configuration);

		ConventionNamingStrategy strategy = conventions.getStrategy();

		assertTrue(strategy instanceof TestConventionNamingStrategy);

	}

}

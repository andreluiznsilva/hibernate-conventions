package hibernate.conventions;

import static org.junit.Assert.assertTrue;
import hibernate.conventions.dummy.TestConventionNamingStrategy;
import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;
import hibernate.conventions.util.ConventionUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MappingConventionsIT {

	private EntityManagerFactory entityManagerFactory;
	private Configuration configuration;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("test");
		configuration = ConventionUtils.extractConfiguration(entityManagerFactory);
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testNormalize() {
		MappingConventions conventions = new MappingConventions(configuration);
		conventions.normalize();
	}

	@Test
	public void testValidate() {
		MappingConventions conventions = new MappingConventions(configuration);
		conventions.validate();
	}

	@Test(expected = RuntimeException.class)
	public void testValidateMaxLength() {

		configuration.setProperty("hibernate.conventions.maxLength", "5");

		MappingConventions conventions = new MappingConventions(configuration);
		conventions.validate();

	}

	@Test
	public void testEmptyNamingStrategy() {

		configuration.setNamingStrategy(null);

		MappingConventions conventions = new MappingConventions(configuration);
		ConventionNamingStrategy strategy = conventions.getStrategy();

		assertTrue(strategy instanceof DefaultConventionNamingStrategy);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConventionNamingStrategy() {

		configuration.setNamingStrategy(new org.hibernate.cfg.DefaultNamingStrategy());

		new MappingConventions(configuration);

	}

	@Test
	public void testConfiguredConventionNamingStrategy() {

		MappingConventions conventions = new MappingConventions(configuration);

		ConventionNamingStrategy strategy = conventions.getStrategy();

		assertTrue(strategy instanceof TestConventionNamingStrategy);

	}

}

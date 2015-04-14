package hibernate.conventions;

import hibernate.conventions.utils.ConventionUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigConventionsIT {

	private EntityManagerFactory entityManagerFactory;
	private Configuration configuration;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("sequence");
		configuration = ConventionUtils.extractConfiguration(entityManagerFactory);
		configuration.setProperty("hibernate.conventions.checkConfig", "true");
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testValidateCheckDisable() {

		configuration.setProperty("hibernate.conventions.checkConfig", "false");

		ConfigConventions conventions = ConfigConventions.create(configuration);
		conventions.validate();

	}

	@Test(expected = RuntimeException.class)
	public void testValidateNoCheckEnable() {
		ConfigConventions conventions = ConfigConventions.create(configuration);
		conventions.validate();
	}

	@Test(expected = RuntimeException.class)
	public void testValidateAutoCommitCheck() {

		configuration.setProperty("hibernate.connection.autocommit", "true");

		ConfigConventions conventions = ConfigConventions.create(configuration);
		conventions.validate();

	}

	@Test
	public void testValidateNoAutoCommitCheck() {

		configuration.setProperty("hibernate.connection.autocommit", "false");

		ConfigConventions conventions = ConfigConventions.create(configuration);
		conventions.validate();

	}

}

package hibernate.conventions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import hibernate.conventions.dummy.TestConventionNamingStrategy;
import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;
import hibernate.conventions.utils.ConventionUtils;

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
		MappingConventions conventions = MappingConventions.create(configuration);
		conventions.normalize();
	}

	@Test
	public void testValidate() {
		MappingConventions conventions = MappingConventions.create(configuration);
		conventions.validate();
	}

	@Test(expected = RuntimeException.class)
	public void testValidateMaxLength() {

		configuration.setProperty("hibernate.conventions.maxLength", "5");

		MappingConventions conventions = MappingConventions.create(configuration);
		conventions.validate();

	}

	@Test
	public void testNormalizeNoCase() {

		MappingConventions conventions = MappingConventions.create(configuration);
		DDLConventions ddl = DDLConventions.create(entityManagerFactory);

		List<String> expecteds = ddl.generateCreateScript();

		conventions.normalize();

		List<String> results = ddl.generateCreateScript();

		assertEquals(expecteds, results);

	}

	@Test
	public void testNormalizeUpperCase() {

		configuration.setProperty("hibernate.conventions.case", "upper");

		MappingConventions conventions = MappingConventions.create(configuration);
		DDLConventions ddl = DDLConventions.create(entityManagerFactory);

		List<String> expecteds = new ArrayList<String>();

		for (String string : ddl.generateCreateScript()) {
			string = string.replace("id", "ID");
			string = string.replace("name", "NAME");
			string = string.replace("DummyEntity", "DUMMYENTITY");
			string = string.replace("seqDUMMYENTITY", "seqDummyEntity");
			expecteds.add(string);
		}

		conventions.normalize();

		List<String> results = ddl.generateCreateScript();

		System.out.println(expecteds);
		System.out.println(results);

		assertEquals(expecteds, results);

	}
	
	@Test
	public void testNormalizeLowerCase() {

		configuration.setProperty("hibernate.conventions.case", "lower");

		MappingConventions conventions = MappingConventions.create(configuration);
		DDLConventions ddl = DDLConventions.create(entityManagerFactory);

		List<String> expecteds = new ArrayList<String>();

		for (String string : ddl.generateCreateScript()) {
			string = string.replace("DummyEntity", "dummyentity");
			string = string.replace("seqdummyentity", "seqDummyEntity");
			expecteds.add(string);
		}

		conventions.normalize();

		List<String> results = ddl.generateCreateScript();

		System.out.println(expecteds);
		System.out.println(results);

		assertEquals(expecteds, results);

	}

	@Test
	public void testEmptyNamingStrategy() {

		configuration.setNamingStrategy(null);

		MappingConventions conventions = MappingConventions.create(configuration);
		ConventionNamingStrategy strategy = conventions.getStrategy();

		assertTrue(strategy instanceof DefaultConventionNamingStrategy);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConventionNamingStrategy() {

		configuration.setNamingStrategy(new org.hibernate.cfg.DefaultNamingStrategy());

		MappingConventions.create(configuration);

	}

	@Test
	public void testConfiguredConventionNamingStrategy() {

		MappingConventions conventions = MappingConventions.create(configuration);

		ConventionNamingStrategy strategy = conventions.getStrategy();

		assertTrue(strategy instanceof TestConventionNamingStrategy);

	}

}

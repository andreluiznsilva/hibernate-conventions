package hibernate.conventions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hibernate.conventions.util.ConventionUtils;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DDLConventionsHSQLDialectIT {

	private EntityManagerFactory entityManagerFactory;
	private Configuration configuration;
	private ServiceRegistry serviceRegistry;
	private DDLConventions conventions;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("test");
		serviceRegistry = ConventionUtils.extractServiceRegistry(entityManagerFactory);
		configuration = ConventionUtils.extractConfiguration(entityManagerFactory);
		conventions = new DDLConventions(configuration, serviceRegistry, new HSQLDialect());
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testGenerateCleanScript() {
		List<String> script = conventions.generateCleanScript();
		assertSql(script,
				"TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
	}

	@Test
	public void testGenerateCreateScript() {
		List<String> script = conventions.generateCreateScript();
		assertSql(script,
				"create table DummyEntity (id bigint not null, name varchar(255), primary key (id))",
				"create sequence seq_DummyEntity start with 1");
	}

	@Test
	public void testGenerateDropScript() {
		List<String> script = conventions.generateDropScript();
		assertSql(script,
				"drop table DummyEntity if exists",
				"drop sequence seq_DummyEntity");
	}

	@Test
	public void testGenerateUpdateScript() {
		List<String> script = conventions.generateUpdateScript();
		assertTrue(script.isEmpty());
	}

	private void assertSql(List<String> script, String... sqls) {
		assertEquals(sqls.length, script.size());
		for (int i = 0; i < sqls.length; i++) {
			assertEquals(sqls[i], script.get(i));
		}
	}

}

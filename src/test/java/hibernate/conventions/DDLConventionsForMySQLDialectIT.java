package hibernate.conventions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.dialect.MySQL5Dialect;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DDLConventionsForMySQLDialectIT {

	private EntityManagerFactory entityManagerFactory;
	private DDLConventions conventions;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("increment");
		conventions = DDLConventions.create(entityManagerFactory, new MySQL5Dialect());
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testGenerateCleanScript() {
		List<String> script = conventions.generateCleanScript();
		assertSql(script,
		        "set foreign_key_checks = 0",
		        "truncate table DummyIncrementEntity",
		        "set foreign_key_checks = 1");
	}

	@Test
	public void testGenerateCreateScript() {
		List<String> script = conventions.generateCreateScript();
		assertSql(script,
		        "create table DummyIncrementEntity (id bigint not null auto_increment, name varchar(255), primary key (id))");
	}

	@Test
	public void testGenerateDropScript() {
		List<String> script = conventions.generateDropScript();
		assertSql(script, "drop table if exists DummyIncrementEntity");
	}

	@Test
	@Ignore
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

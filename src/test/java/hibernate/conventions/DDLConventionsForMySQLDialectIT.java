package hibernate.conventions;

import static hibernate.conventions.test.TestUtils.assertSql;
import static hibernate.conventions.test.TestUtils.execute;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.dialect.MySQL5Dialect;
import org.junit.After;
import org.junit.Before;
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
	public void testGenerateUpdateScript() {
		execute("drop table if exists DummyIncrementEntity", entityManagerFactory);
		List<String> script = conventions.generateUpdateScript();
		assertSql(script, "create table DummyIncrementEntity (id bigint not null auto_increment, name varchar(255), primary key (id))");
	}

}

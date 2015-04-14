package hibernate.conventions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.dialect.Oracle10gDialect;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DDLConventionsForOracle10DialectIT {

	private EntityManagerFactory entityManagerFactory;
	private DDLConventions conventions;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("sequence");
		conventions = DDLConventions.create(entityManagerFactory, new Oracle10gDialect());
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testGenerateCleanScript() {
		List<String> script = conventions.generateCleanScript();
		assertSql(script, "truncate table DummySequenceEntity");
	}

	@Test
	public void testGenerateCreateScript() {
		List<String> script = conventions.generateCreateScript();
		assertSql(script,
		        "create table DummySequenceEntity (id bigint not null, name varchar(255), primary key (id))",
		        "create sequence seqDummySequenceEntity");
	}

	@Test
	public void testGenerateDropScript() {
		List<String> script = conventions.generateDropScript();
		assertSql(script,
		        "drop table DummySequenceEntity cascade constraints",
		        "drop sequence seqDummySequenceEntity");
	}

	@Test
	@Ignore
	// TODO: Verificar exceção
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
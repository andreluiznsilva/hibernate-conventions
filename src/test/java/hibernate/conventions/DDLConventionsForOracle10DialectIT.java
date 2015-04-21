package hibernate.conventions;

import static hibernate.conventions.test.TestUtils.assertSql;
import static hibernate.conventions.test.TestUtils.execute;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.dialect.Oracle10gDialect;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DDLConventionsForOracle10DialectIT {

	private DDLConventions conventions;
	private EntityManagerFactory entityManagerFactory;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("sequence");
		conventions = DDLConventions.create(entityManagerFactory, new Oracle10gDialect() {

			@Override
			public String getQuerySequencesString() {
				return null;
			}

		});
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
	public void testGenerateUpdateScript() {
		execute("drop sequence seqDummySequenceEntity", entityManagerFactory);
		List<String> script = conventions.generateUpdateScript();
		assertSql(script, "create sequence seqDummySequenceEntity");
	}

}

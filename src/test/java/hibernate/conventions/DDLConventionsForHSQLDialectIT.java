package hibernate.conventions;

import static hibernate.conventions.test.TestUtils.*;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.dialect.HSQLDialect;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DDLConventionsForHSQLDialectIT {

	private EntityManagerFactory entityManagerFactory;
	private DDLConventions conventions;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("sequence");
		conventions = DDLConventions.create(entityManagerFactory, new HSQLDialect());
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testGenerateCleanScript() {
		List<String> script = conventions.generateCleanScript();
		assertSql(script, "truncate schema public restart identity and commit no check");
	}

	@Test
	public void testGenerateCreateScript() {
		List<String> script = conventions.generateCreateScript();
		assertSql(script,
		        "create table DummySequenceEntity (id bigint not null, name varchar(255), primary key (id))",
		        "create sequence seqDummySequenceEntity start with 1");
	}

	@Test
	public void testGenerateDropScript() {
		List<String> script = conventions.generateDropScript();
		assertSql(script,
		        "drop table DummySequenceEntity if exists",
		        "drop sequence seqDummySequenceEntity");
	}

	@Test
	public void testGenerateUpdateScript() {
		execute("drop sequence seqDummySequenceEntity", entityManagerFactory);
		List<String> script = conventions.generateUpdateScript();
		assertSql(script, "create sequence seqDummySequenceEntity start with 1");
	}

}

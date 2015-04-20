package hibernate.conventions.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScriptGeneratorToolTest {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("sequence");
	}

	@After
	public void tearDown() throws Exception {
		entityManagerFactory.close();
	}

	@Test
	public void testGenerateEmpty() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new ScriptGeneratorTool(entityManagerFactory).generate(out);

		String result = new String(out.toByteArray(), "UTF-8");

		assertEquals("", result);

	}

	@Test
	public void testGenerateClean() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new ScriptGeneratorTool(entityManagerFactory).generate(out, "clean");

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains("truncate schema public restart identity and commit no check"));

	}

	@Test
	public void testGenerateDrop() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new ScriptGeneratorTool(entityManagerFactory).generate(out, "drop");

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains("drop table DummySequenceEntity"));
		assertTrue(result.contains("drop sequence seqDummySequenceEntity"));

	}

	@Test
	public void testGenerateCreate() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new ScriptGeneratorTool(entityManagerFactory).generate(out, "create");

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains("create table DummySequenceEntity"));
		assertTrue(result.contains("create sequence seqDummySequenceEntity"));

	}

	@Test
	public void testGenerateUpdate() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new ScriptGeneratorTool(entityManagerFactory).generate(out, "create");

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains("create table DummySequenceEntity"));
		assertTrue(result.contains("create sequence seqDummySequenceEntity"));

	}

}

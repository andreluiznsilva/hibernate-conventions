package hibernate.conventions.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hibernate.conventions.dummy.DummyIncrementEntity;
import hibernate.conventions.dummy.DummySequenceEntity;
import hibernate.conventions.test.TestEntity;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class MappingGeneratorToolTest {

	@Test
	public void testGenerateEmpty() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new MappingGeneratorTool("hibernate.conventions.generator").generate(out);

		String result = new String(out.toByteArray(), "UTF-8");

		assertFalse(result.contains("class"));

	}

	@Test
	public void testGenerateTwoEntities() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new MappingGeneratorTool("hibernate.conventions.dummy").generate(out);

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains(DummyIncrementEntity.class.getName()));
		assertTrue(result.contains(DummySequenceEntity.class.getName()));

	}

	@Test
	public void testGenerateTwoPackages() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new MappingGeneratorTool("hibernate.conventions.dummy", "hibernate.conventions.test").generate(out);

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains(DummyIncrementEntity.class.getName()));
		assertTrue(result.contains(DummySequenceEntity.class.getName()));
		assertTrue(result.contains(TestEntity.class.getName()));

	}

	@Test
	public void testGenerateParentPackage() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new MappingGeneratorTool("hibernate.conventions").generate(out);

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains(DummyIncrementEntity.class.getName()));
		assertTrue(result.contains(DummySequenceEntity.class.getName()));
		assertTrue(result.contains(TestEntity.class.getName()));

	}

	@Test
	public void testGenerateNoPackage() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		new MappingGeneratorTool().generate(out);

		String result = new String(out.toByteArray(), "UTF-8");

		assertTrue(result.contains(DummyIncrementEntity.class.getName()));
		assertTrue(result.contains(DummySequenceEntity.class.getName()));
		assertTrue(result.contains(TestEntity.class.getName()));

	}

}

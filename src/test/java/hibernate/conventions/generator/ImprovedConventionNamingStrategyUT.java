package hibernate.conventions.generator;

import static org.junit.Assert.assertEquals;
import hibernate.conventions.dummy.DummySequenceEntity;
import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.ImprovedConventionNamingStrategy;

import org.junit.Test;

public class ImprovedConventionNamingStrategyUT {

	private ConventionNamingStrategy strategy = new ImprovedConventionNamingStrategy();

	@Test
	public void testClassToTableName() {

		String className = DummySequenceEntity.class.getName();

		String result = strategy.classToTableName(className);

		assertEquals("dummy_sequence_entity", result);

	}

	@Test
	public void testPrimaryKeyName() {

		String entity = DummySequenceEntity.class.getSimpleName();
		String tableName = entity;

		String result = strategy.primaryKeyName(entity, tableName);

		assertEquals("pk_dummy_sequence_entity", result);

	}

}

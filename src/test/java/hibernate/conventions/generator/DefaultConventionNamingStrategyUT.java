package hibernate.conventions.generator;

import static org.junit.Assert.assertEquals;
import hibernate.conventions.dummy.DummySequenceEntity;
import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;

import org.junit.Test;

public class DefaultConventionNamingStrategyUT {

	private ConventionNamingStrategy strategy = new DefaultConventionNamingStrategy();

	@Test
	public void testClassToTableName() {

		String className = DummySequenceEntity.class.getName();

		String result = strategy.classToTableName(className);

		assertEquals("DummySequenceEntity", result);

	}

	@Test
	public void testPrimaryKeyName() {

		String entity = DummySequenceEntity.class.getSimpleName();
		String tableName = entity;

		String result = strategy.primaryKeyName(entity, tableName);

		assertEquals("pk" + tableName, result);

	}

}

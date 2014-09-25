package hibernate.conventions.generator;

import static org.junit.Assert.assertEquals;
import hibernate.conventions.dummy.DummyEntity;
import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;

import org.junit.Test;

public class DefaultConventionNamingStrategyUT {

	private ConventionNamingStrategy strategy = new DefaultConventionNamingStrategy();

	@Test
	public void testClassToTableName() {

		String className = DummyEntity.class.getName();

		String result = strategy.classToTableName(className);

		assertEquals("DummyEntity", result);

	}

	@Test
	public void testPrimaryKeyName() {

		String entity = DummyEntity.class.getSimpleName();
		String tableName = entity;

		String result = strategy.primaryKeyName(entity, tableName);

		assertEquals("pk_" + tableName, result);

	}

}

package hibernate.conventions.generator;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;
import static org.hibernate.id.PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER;
import static org.hibernate.id.PersistentIdentifierGenerator.TABLE;
import static org.hibernate.id.SequenceGenerator.SEQUENCE;
import static org.junit.Assert.assertEquals;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;

import java.util.Properties;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;
import org.junit.Test;

public class ConventionSequenceGeneratorUT {

	private SequenceGenerator generator = new ConventionSequenceGenerator();

	private ObjectNameNormalizer normalizer = new ObjectNameNormalizer() {

		@Override
		protected boolean isUseQuotedIdentifiersGlobally() {
			return false;
		}

		@Override
		protected NamingStrategy getNamingStrategy() {
			return new DefaultConventionNamingStrategy();
		}

	};

	@Test
	public void testConfigureNoTableName() {

		String entityName = "a";

		Type type = null;
		Dialect dialect = new Oracle10gDialect();

		Properties params = new Properties();
		params.put(IDENTIFIER_NORMALIZER, normalizer);
		params.put(ENTITY_NAME, entityName);

		generator.configure(type, params, dialect);

		assertEquals("seq" + entityName, params.get(SEQUENCE));

	}

	@Test
	public void testConfigureTypePropertiesDialect() {

		String entityName = "a";
		String tableName = "b";

		Type type = null;
		Dialect dialect = new Oracle10gDialect();

		Properties params = new Properties();
		params.put(org.hibernate.id.PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, normalizer);
		params.put(TABLE, tableName);
		params.put(ENTITY_NAME, entityName);

		generator.configure(type, params, dialect);

		assertEquals("seq" + tableName, params.get(SEQUENCE));

	}

}

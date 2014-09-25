package hibernate.conventions.generator;


import static org.hibernate.id.PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER;
import static org.hibernate.id.PersistentIdentifierGenerator.TABLE;
import static org.hibernate.id.SequenceGenerator.SEQUENCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import hibernate.conventions.generator.TableSequenceGenerator;

import java.util.Properties;

import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;
import org.junit.Test;

public class TableSequenceGeneratorUT {

	private SequenceGenerator generator = new TableSequenceGenerator();

	private ObjectNameNormalizer normalizer = new ObjectNameNormalizer() {

		@Override
		protected boolean isUseQuotedIdentifiersGlobally() {
			return false;
		}

		@Override
		protected NamingStrategy getNamingStrategy() {
			return null;
		}

	};

	@Test
	public void testConfigureNoTableName() {

		Type type = null;
		Dialect dialect = new Oracle10gDialect();

		Properties params = new Properties();
		params.put(IDENTIFIER_NORMALIZER, normalizer);

		generator.configure(type, params, dialect);

		assertNull(params.get(SEQUENCE));

	}

	@Test
	public void testConfigureTypePropertiesDialect() {

		String tableName = "test";

		Type type = null;
		Dialect dialect = new Oracle10gDialect();

		Properties params = new Properties();
		params.put(org.hibernate.id.PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, normalizer);
		params.put(TABLE, tableName);

		generator.configure(type, params, dialect);

		assertEquals("seq_" + tableName, params.get(SEQUENCE));

	}

}

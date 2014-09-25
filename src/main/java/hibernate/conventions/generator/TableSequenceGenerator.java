package hibernate.conventions.generator;

import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.util.ReflectionUtils;

import java.util.Properties;

import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

public class TableSequenceGenerator extends SequenceGenerator {

	@Override
	public void configure(Type type, Properties params, Dialect dialect) {

		String entityName = params.getProperty(ENTITY_NAME);
		String table = params.getProperty(TABLE);
		ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params.get(IDENTIFIER_NORMALIZER);

		ConventionNamingStrategy strategy = extractNameStrategy(normalizer);

		String sequence = params.getProperty(SEQUENCE);
		if (sequence == null || sequence.isEmpty()) {
			sequence = strategy.sequenceName(entityName, table);
			params.setProperty(SEQUENCE, sequence);
		}

		super.configure(type, params, dialect);

	}

	private ConventionNamingStrategy extractNameStrategy(ObjectNameNormalizer normalizer) {
		return (ConventionNamingStrategy) ReflectionUtils.getPropertyValue("namingStrategy", normalizer);
	}

}
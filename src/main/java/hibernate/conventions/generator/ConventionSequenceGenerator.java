package hibernate.conventions.generator;

import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.utils.ConventionUtils;

import java.util.Properties;

import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

public class ConventionSequenceGenerator extends SequenceGenerator {

	@Override
	public void configure(Type type, Properties params, Dialect dialect) {

		String sequence = params.getProperty(SEQUENCE);

		if (ConventionUtils.isEmpty(sequence)) {

			String entityName = params.getProperty(ENTITY_NAME);
			String table = params.getProperty(TABLE);

			ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params.get(IDENTIFIER_NORMALIZER);
			ConventionNamingStrategy strategy = ConventionUtils.extractNameStrategy(normalizer);

			sequence = strategy.sequenceName(entityName, table);

			params.setProperty(SEQUENCE, sequence);

		}

		super.configure(type, params, dialect);

	}

}
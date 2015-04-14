package hibernate.conventions.strategy;

import org.hibernate.cfg.NamingStrategy;

public interface ConventionNamingStrategy extends NamingStrategy {

	String primaryKeyName(String entity, String tableName);

	String foreignKeyName(String ownerEntity, String ownerEntityTable,
	        String associatedEntity, String associatedEntityTable);

	String uniqueKeyName(String entity, String tableName);

	String indexName(String entity, String tableName);

	String sequenceName(String entity, String tableName);

	String sqlType(String name, String sqlType);

	int sqlPrecision(String name, String sqlType, int precision);

	int sqlScale(String name, String sqlType, int scale);

}
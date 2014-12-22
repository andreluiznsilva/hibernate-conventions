package hibernate.conventions.strategy;

import hibernate.conventions.utils.ConventionUtils;

import org.hibernate.cfg.DefaultNamingStrategy;

public class DefaultConventionNamingStrategy extends DefaultNamingStrategy implements ConventionNamingStrategy {

	@Override
	public String foreignKeyColumnName(
	        String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
		return propertyTableName + referencedColumnName;
	}

	@Override
	public String foreignKeyName(String ownerEntity, String ownerEntityTable, String associatedEntity,
	        String associatedEntityTable) {
		return "fk" + ownerEntityTable + associatedEntityTable;
	}

	@Override
	public String indexName(String entity, String tableName) {
		return "idx" + tableName;
	}

	@Override
	public String primaryKeyName(String entity, String tableName) {
		return "pk" + tableName;
	}

	@Override
	public String sequenceName(String entity, String tableName) {
		return "seq" + (ConventionUtils.isEmpty(tableName) ? entity : tableName);
	}

	@Override
	public int sqlPrecision(String name, String sqlType, int precision) {
		return precision;
	}

	@Override
	public int sqlScale(String name, String sqlType, int scale) {
		return scale;
	}

	@Override
	public String sqlType(String name, String sqlType) {
		return sqlType;
	}

	@Override
	public String uniqueKeyName(String entity, String tableName) {
		return "uk" + tableName;
	}

}

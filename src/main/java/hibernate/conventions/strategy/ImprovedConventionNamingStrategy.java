package hibernate.conventions.strategy;

import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.utils.ConventionUtils;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class ImprovedConventionNamingStrategy extends ImprovedNamingStrategy implements ConventionNamingStrategy {

	@Override
	public String foreignKeyColumnName(
	        String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
		return addUnderscores(propertyTableName + "_" + referencedColumnName);
	}

	public String foreignKeyName(String ownerEntity, String ownerEntityTable, String associatedEntity,
	        String associatedEntityTable) {
		return addUnderscores("fk_" + ownerEntityTable + "_" + associatedEntityTable);
	}

	public String indexName(String entity, String tableName) {
		return addUnderscores("idx_" + tableName);
	}

	public String primaryKeyName(String entity, String tableName) {
		return addUnderscores("pk_" + tableName);
	}

	public String sequenceName(String entity, String tableName) {
		return addUnderscores("seq_" + (ConventionUtils.isEmpty(tableName) ? entity : tableName));
	}

	public int sqlPrecision(String name, String sqlType, int precision) {
		return precision;
	}

	public int sqlScale(String name, String sqlType, int scale) {
		return scale;
	}

	public String sqlType(String name, String sqlType) {
		return sqlType;
	}

	public String uniqueKeyName(String entity, String tableName) {
		return addUnderscores("uk_" + tableName);
	}

}

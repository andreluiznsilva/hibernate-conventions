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

	@Override
	public String foreignKeyName(String ownerEntity, String ownerEntityTable, String associatedEntity,
	        String associatedEntityTable) {
		return addUnderscores("fk_" + ownerEntityTable + "_" + associatedEntityTable);
	}

	@Override
	public String indexName(String entity, String tableName) {
		return addUnderscores("idx_" + tableName);
	}

	@Override
	public String primaryKeyName(String entity, String tableName) {
		return addUnderscores("pk_" + tableName);
	}

	@Override
	public String sequenceName(String entity, String tableName) {
		return addUnderscores("seq_" + (ConventionUtils.isEmpty(tableName) ? entity : tableName));
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
		return addUnderscores("uk_" + tableName);
	}

}

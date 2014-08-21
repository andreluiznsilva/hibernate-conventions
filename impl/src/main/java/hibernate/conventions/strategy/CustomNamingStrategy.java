package hibernate.conventions.strategy;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class CustomNamingStrategy extends ImprovedNamingStrategy {

	/*
	 * http://www.postgresql.org/docs/current/interactive/sql-syntax-lexical.html
	 * #SQL-SYNTAX-IDENTIFIERS
	 * The system uses no more than NAMEDATALEN-1 bytes of an identifier; longer
	 * names can be written in commands, but they will be truncated. By default,
	 * NAMEDATALEN is 64 so the maximum identifier length is 63 bytes. If this
	 * limit is problematic, it can be raised by changing the NAMEDATALEN
	 * constant in src/include/pg_config_manual.h.
	 */
	private static final int SQL_NAMES_MAX_LENGTH = 63;

	@Override
	public String classToTableName(String className) {
		return normalize(super.classToTableName(className));
	}

	@Override
	public String collectionTableName(
			String ownerEntity, String ownerEntityTable, String associatedEntity,
			String associatedEntityTable, String propertyName) {

		String result = super.collectionTableName(
				ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable, propertyName);

		return normalize(result);

	}

	@Override
	public String columnName(String columnName) {
		return normalize(super.columnName(columnName));
	}

	@Override
	public String foreignKeyColumnName(
			String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {

		String result = super.foreignKeyColumnName(
				propertyName, propertyEntityName, propertyTableName, referencedColumnName);

		result = result + "_" + referencedColumnName;

		return normalize(result);

	}

	@Override
	public String joinKeyColumnName(String joinedColumn, String joinedTable) {
		String result = super.joinKeyColumnName(joinedColumn, joinedTable);
		return normalize(result);
	}

	@Override
	public String propertyToColumnName(String propertyName) {
		return normalize(super.propertyToColumnName(propertyName));
	}

	@Override
	public String tableName(String tableName) {
		return normalize(super.tableName(tableName));
	}

	private String normalize(String name) {

		name = name.replaceAll("\"", "");

		if (name.length() > 30) {
			throw new IllegalArgumentException("Nome \"" + name + "\" possui mais de "
					+ SQL_NAMES_MAX_LENGTH + " caracteres!");
		}

		name = "\"" + name + "\"";

		return name.toLowerCase();

	}

}
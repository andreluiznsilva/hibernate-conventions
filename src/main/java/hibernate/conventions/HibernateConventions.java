package hibernate.conventions;

import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;

import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

public class HibernateConventions {

	private final Configuration configuration;
	private final ConventionNamingStrategy strategy;

	public HibernateConventions(Configuration configuration) {
		this.configuration = configuration;
		strategy = new DefaultConventionNamingStrategy();
	}

	public void normalize() {

		Iterator<PersistentClass> iterator1 = configuration.getClassMappings();
		while (iterator1.hasNext()) {
			PersistentClass clazz = iterator1.next();
			Table table = clazz.getTable();
			normalize(table, clazz.getEntityName());
		}

		Iterator<?> iterator2 = configuration.getCollectionMappings();
		while (iterator2.hasNext()) {
			Collection collection = (Collection) iterator2.next();
			Table table = collection.getCollectionTable();
			normalize(table, collection.getOwnerEntityName());
		}

	}

	public void validate() {

		Iterator<PersistentClass> iterator = configuration.getClassMappings();
		while (iterator.hasNext()) {
			PersistentClass clazz = iterator.next();
			Table table = clazz.getTable();
			validate(table);
		}

		Iterator<?> iterator2 = configuration.getCollectionMappings();
		while (iterator2.hasNext()) {
			Collection collection = (Collection) iterator2.next();
			Table table = collection.getCollectionTable();
			validate(table);
		}

	}

	private int getProperty(String name, int defaultValue) {
		String value = (String) configuration.getProperties().get(name);
		return value == null ? defaultValue : Integer.valueOf(value);
	}

	private void normalize(Table table, String entityName) {
		normalizeColumns(table, entityName);
		normalizePrimaryKeys(table, entityName);
		normalizeForeignKeys(table, entityName);
		normalizeUniqueKeys(table, entityName);
		normalizeIndexs(table, entityName);
	}

	@SuppressWarnings("rawtypes")
	private void normalizeColumns(Table table, String entityName) {

		Iterator iterator = table.getColumnIterator();
		while (iterator.hasNext()) {

			Column column = (Column) iterator.next();

			String sqlType = strategy.sqlType(table.getName(), column.getSqlType());
			int sqlPrecision = strategy.sqlPrecision(table.getName(), column.getSqlType(), column.getPrecision());
			int sqlScale = strategy.sqlScale(table.getName(), column.getSqlType(), column.getScale());

			column.setSqlType(sqlType);
			column.setPrecision(sqlPrecision);
			column.setScale(sqlScale);

		}

	}

	@SuppressWarnings("rawtypes")
	private void normalizeForeignKeys(Table table, String entityName) {
		Iterator iterator = table.getForeignKeyIterator();
		while (iterator.hasNext()) {
			ForeignKey fk = (ForeignKey) iterator.next();
			String name = strategy.foreignKeyName(entityName, table.getName(),
					fk.getReferencedEntityName(), fk.getReferencedTable().getName());
			fk.setName(name);
		}
	}

	@SuppressWarnings("rawtypes")
	private void normalizeIndexs(Table table, String entityName) {
		Iterator iterator = table.getIndexIterator();
		while (iterator.hasNext()) {
			Index idx = (Index) iterator.next();
			String name = strategy.indexName(entityName, table.getName());
			idx.setName(name);
		}
	}

	private void normalizePrimaryKeys(Table table, String entityName) {
		PrimaryKey pk = table.getPrimaryKey();
		if (pk != null) {
			String name = strategy.primaryKeyName(entityName, table.getName());
			pk.setName(name);
		}
	}

	@SuppressWarnings("rawtypes")
	private void normalizeUniqueKeys(Table table, String entityName) {
		Iterator iterator = table.getUniqueKeyIterator();
		while (iterator.hasNext()) {
			UniqueKey uk = (UniqueKey) iterator.next();
			String name = strategy.uniqueKeyName(entityName, table.getName());
			uk.setName(name);
		}
	}

	private void validate(Table table) {
		validateTable(table);
		validateColumns(table);
		validatePrimaryKeys(table);
		validateForeignKeys(table);
		validateUniqueKeys(table);
		validateIndexs(table);
	}

	@SuppressWarnings("rawtypes")
	private void validateColumns(Table table) {
		Iterator iterator = table.getColumnIterator();
		while (iterator.hasNext()) {
			Column column = (Column) iterator.next();
			validateMaxLength(column.getName());
		}
	}

	@SuppressWarnings("rawtypes")
	private void validateForeignKeys(Table table) {
		Iterator iterator = table.getForeignKeyIterator();
		while (iterator.hasNext()) {
			ForeignKey fk = (ForeignKey) iterator.next();
			validateMaxLength(fk.getName());
		}
	}

	@SuppressWarnings("rawtypes")
	private void validateIndexs(Table table) {
		Iterator iterator = table.getIndexIterator();
		while (iterator.hasNext()) {
			Index index = (Index) iterator.next();
			validateMaxLength(index.getName());
		}
	}

	private void validateMaxLength(String name) {
		int maxLength = getProperty("hibernate.conventions.maxLength", 255);
		if (name.length() > maxLength) {
			throw new RuntimeException("Name '" + name + "' has more than " + maxLength + " caracteres");
		}
	}

	private void validatePrimaryKeys(Table table) {
		PrimaryKey primaryKey = table.getPrimaryKey();
		if (primaryKey != null) {
			validateMaxLength(primaryKey.getName());
		}
	}

	private void validateTable(Table table) {
		validateMaxLength(table.getName());
	}

	@SuppressWarnings("rawtypes")
	private void validateUniqueKeys(Table table) {
		Iterator iterator = table.getUniqueKeyIterator();
		while (iterator.hasNext()) {
			UniqueKey uk = (UniqueKey) iterator.next();
			validateMaxLength(uk.getName());
		}
	}

}

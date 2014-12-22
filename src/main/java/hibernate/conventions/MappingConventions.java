package hibernate.conventions;

import static hibernate.conventions.utils.ConventionUtils.extractConfiguration;
import hibernate.conventions.strategy.ConventionNamingStrategy;
import hibernate.conventions.strategy.DefaultConventionNamingStrategy;

import java.util.Iterator;

import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;

public class MappingConventions {

	public static MappingConventions create(Configuration configuration) {
		return new MappingConventions(configuration);
	}

	public static MappingConventions create(EntityManagerFactory entityManagerFactory) {
		return create(extractConfiguration(entityManagerFactory));
	}

	private final Configuration configuration;
	private final ConventionNamingStrategy strategy;

	private MappingConventions(Configuration configuration) {

		this.configuration = configuration;

		NamingStrategy namingStrategy = configuration.getNamingStrategy();

		if (namingStrategy == null) {
			strategy = new DefaultConventionNamingStrategy();
		} else if (namingStrategy instanceof ConventionNamingStrategy) {
			strategy = (ConventionNamingStrategy) namingStrategy;
		} else {
			throw new IllegalArgumentException(
			        "Configured namingStrategy is not a instance of ConventionNamingStrategy");
		}

	}

	private int getProperty(String name, int defaultValue) {
		return Integer.valueOf(getProperty(name, Integer.toString(defaultValue)));
	}

	private String getProperty(String name, String defaultValue) {
		String value = (String) configuration.getProperties().get(name);
		return value == null ? defaultValue : value;
	}

	public ConventionNamingStrategy getStrategy() {
		return strategy;
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

	private void normalize(Table table, String entityName) {
		normalizeTable(table, entityName);
		normalizeColumns(table, entityName);
		normalizePrimaryKeys(table, entityName);
		normalizeForeignKeys(table, entityName);
		normalizeUniqueKeys(table, entityName);
		normalizeIndexs(table, entityName);
	}

	private String normalizeCase(String name) {
		String caze = getProperty("hibernate.conventions.case", null);
		if ("upper".equals(caze)) {
			name = name.toUpperCase();
		} else if ("lower".equals(caze)) {
			name = name.toLowerCase();
		}
		return name;
	}

	@SuppressWarnings("rawtypes")
	private void normalizeColumns(Table table, String entityName) {

		Iterator iterator = table.getColumnIterator();
		while (iterator.hasNext()) {

			Column column = (Column) iterator.next();

			String name = normalizeCase(column.getName());
			String sqlType = strategy.sqlType(table.getName(), column.getSqlType());
			int sqlPrecision = strategy.sqlPrecision(table.getName(), column.getSqlType(), column.getPrecision());
			int sqlScale = strategy.sqlScale(table.getName(), column.getSqlType(), column.getScale());

			column.setName(name);
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
			name = normalizeCase(name);
			fk.setName(name);
		}
	}

	@SuppressWarnings("rawtypes")
	private void normalizeIndexs(Table table, String entityName) {
		Iterator iterator = table.getIndexIterator();
		while (iterator.hasNext()) {
			Index idx = (Index) iterator.next();
			String name = strategy.indexName(entityName, table.getName());
			name = normalizeCase(name);
			idx.setName(name);
		}
	}

	private void normalizePrimaryKeys(Table table, String entityName) {
		PrimaryKey pk = table.getPrimaryKey();
		if (pk != null) {
			String name = strategy.primaryKeyName(entityName, table.getName());
			name = normalizeCase(name);
			pk.setName(name);
		}
	}

	private void normalizeTable(Table table, String entityName) {
		String name = normalizeCase(table.getName());
		table.setName(name);
	}

	@SuppressWarnings("rawtypes")
	private void normalizeUniqueKeys(Table table, String entityName) {
		Iterator iterator = table.getUniqueKeyIterator();
		while (iterator.hasNext()) {
			UniqueKey uk = (UniqueKey) iterator.next();
			String name = strategy.uniqueKeyName(entityName, table.getName());
			name = normalizeCase(name);
			uk.setName(name);
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

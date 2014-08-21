package hibernate.conventions;

import hibernate.conventions.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;

public class MappingConventions implements Integrator {

	public static List<String> generateCleanScript(EntityManager entityManager) {

		List<String> results = new ArrayList<String>();

		String name = getDialect(entityManager).getClass().getSimpleName().toUpperCase();

		if (name.startsWith("HSQL")) {
			results.add("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
		} else if (name.startsWith("POSTGRESQL")) {

			Configuration configuration = getConfiguration(entityManager);

			Iterator<PersistentClass> iterator1 = configuration.getClassMappings();
			while (iterator1.hasNext()) {
				PersistentClass clazz = iterator1.next();
				Table table = clazz.getTable();
				results.add("TRUNCATE " + table.getName() + " CASCADE");
			}

			Iterator<?> iterator2 = configuration.getCollectionMappings();
			while (iterator2.hasNext()) {
				Collection collection = (Collection) iterator2.next();
				Table table = collection.getCollectionTable();
				results.add("TRUNCATE " + table.getName() + " CASCADE");
			}

		}

		return results;

	}

	public static List<String> generateCreateScript(EntityManager entityManager) {
		return generate(entityManager, 'c');
	}

	public static List<String> generateDropScript(EntityManager entityManager) {
		return generate(entityManager, 'd');
	}

	public static List<String> generateUpdateScript(EntityManager entityManager) {
		return generate(entityManager, 'u');
	}

	public static void normalize(EntityManager entityManager) {
		normalize(getConfiguration(entityManager));
	}

	public static void validate(EntityManager entityManager) {
		validate(getConfiguration(entityManager));
	}

	private static void addAll(String[] origin, List<String> target) {
		target.addAll(Arrays.asList(origin));
	}

	private static void checkSize(String name) {
		Long maxLength = Long.valueOf(getConfig().getProperty("conventions.name.maxLength"));
		if (name.length() > maxLength) {
			throw new RuntimeException("Name '" + name + "' tem mais que " + maxLength + " caracteres");
		}
	}

	private static void checkSize(Table table) {
		Long maxLength = Long.valueOf(getConfig().getProperty("conventions.name.maxLength"));
		if (table.getName().length() > maxLength) {
			throw new RuntimeException("Table " + table.getName() + " tem mais de " + maxLength + " caracteres");
		}
	}

	private static List<String> generate(EntityManager entityManager, char type) {

		List<String> sqls = new ArrayList<String>();

		Configuration config = MappingConventions.getConfiguration(entityManager);
		Dialect dialect = MappingConventions.getDialect(entityManager);

		switch (type) {
			case 'c':
				addAll(config.generateSchemaCreationScript(dialect), sqls);
				break;
			case 'd':
				addAll(config.generateDropSchemaScript(dialect), sqls);
				break;
			case 'u':
				DatabaseMetadata databaseMetadata = getData(entityManager, dialect, config);
				List<SchemaUpdateScript> scripts = config.generateSchemaUpdateScriptList(dialect, databaseMetadata);
				for (SchemaUpdateScript schemaUpdateScript : scripts) {
					sqls.add(schemaUpdateScript.getScript());
				}
				break;
		}

		return sqls;

	}

	private static Properties getConfig() {
		return loadPropertiesInClasspath("mappingConventions.properties");
	}

	public static InputStream getResourceAsStreamInClasspath(String resource) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
	}

	public static Properties loadPropertiesInClasspath(String file) {
		InputStream stream = null;
		try {

			stream = getResourceAsStreamInClasspath(file);

			if (stream == null) {
				throw new IOException("File " + file + " not found");
			}

			Properties config = new Properties();
			config.load(stream);
			return config;

		} catch (IOException e) {
			throw new RuntimeException("Error loading " + file, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}

	private static Configuration getConfiguration(EntityManager entityManager) {
		return (Configuration) ReflectionUtils.getFieldValue("configuration", getServiceRegistry(entityManager));
	}

	private static DatabaseMetadata getData(EntityManager entityManager, Dialect dialect, Configuration configuration) {
		try {
			SessionFactory sessionFactory = entityManager.unwrap(Session.class).getSessionFactory();
			ServiceRegistry registry = (ServiceRegistry) ReflectionUtils.getFieldValue("serviceRegistry",
					sessionFactory);
			Connection connection = registry.getService(ConnectionProvider.class).getConnection();
			return new DatabaseMetadata(connection, dialect, configuration);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static Dialect getDialect(EntityManager entityManager) {
		return (Dialect) ReflectionUtils.getFieldValue("dialect", getSessionFactory(entityManager));
	}

	private static ServiceRegistry getServiceRegistry(EntityManager entityManager) {
		return (ServiceRegistry) ReflectionUtils.getFieldValue("serviceRegistry", getSessionFactory(entityManager));
	}

	private static SessionFactory getSessionFactory(EntityManager entityManager) {
		return entityManager.unwrap(Session.class).getSessionFactory();
	}

	private static void normalize(Configuration configuration) {

		Iterator<PersistentClass> iterator1 = configuration.getClassMappings();

		while (iterator1.hasNext()) {
			PersistentClass clazz = iterator1.next();
			Table table = clazz.getTable();
			normalizeClass(clazz);
			normalizeColumns(table);
			normalizePrimaryKeys(table);
			normalizeForeignKeys(table);
			normalizeUniqueKeys(table);
			normalizeIndexs(table);
		}

		Iterator<?> iterator2 = configuration.getCollectionMappings();
		while (iterator2.hasNext()) {
			Collection collection = (Collection) iterator2.next();
			Table table = collection.getCollectionTable();
			checkSize(table);
			normalizeColumns(table);
			normalizePrimaryKeys(table);
			normalizeForeignKeys(table);
			normalizeIndexs(table);
		}

	}

	private static void normalizeClass(PersistentClass persistentClass) {
		Table table = persistentClass.getTable();
		Class<?> clazz = ReflectionUtils.loadClass(persistentClass.getEntityName());
		String comment = getConfig().getProperty("conventions.comments." + table.getName(), clazz.getSimpleName());
		table.setComment(comment);
	}

	@SuppressWarnings("rawtypes")
	private static void normalizeColumns(Table table) {

		Properties config = getConfig();

		Iterator uniqueKeyIterator = table.getColumnIterator();
		while (uniqueKeyIterator.hasNext()) {

			Column column = (Column) uniqueKeyIterator.next();
			String sqlType = column.getSqlType();

			if (sqlType != null) {
				String type = config.getProperty("conventions.type." + sqlType, sqlType);
				column.setSqlType(type);
			}

			String comment = config.getProperty(
					"conventions.comments." + table.getName() + "." + column.getName(), column.getName());
			column.setComment(comment);

			checkSize(column.getName());

		}
	}

	@SuppressWarnings("rawtypes")
	private static void normalizeForeignKeys(Table table) {
		Iterator iterator = table.getForeignKeyIterator();
		while (iterator.hasNext()) {
			ForeignKey fk = (ForeignKey) iterator.next();
			String name = "FK_" + fk.getTable().getName() + "_" + fk.getReferencedTable().getName();
			checkSize(name);
			fk.setName(name.toLowerCase());
		}
	}

	@SuppressWarnings("rawtypes")
	private static void normalizeIndexs(Table table) {
		Iterator iterator = table.getIndexIterator();
		int count = 0;
		while (iterator.hasNext()) {
			Index index = (Index) iterator.next();
			String name = "ID_" + index.getTable().getName() + "__" + count++;
			checkSize(name);
			index.setName(name.toLowerCase());
		}

	}

	private static void normalizePrimaryKeys(Table table) {
		PrimaryKey primaryKey = table.getPrimaryKey();
		if (primaryKey != null) {
			String name = "PK_" + table.getName();
			checkSize(name);
			primaryKey.setName(name.toLowerCase());
		}
	}

	@SuppressWarnings("rawtypes")
	private static void normalizeUniqueKeys(Table table) {
		Iterator iterator = table.getUniqueKeyIterator();
		while (iterator.hasNext()) {
			UniqueKey uk = (UniqueKey) iterator.next();
			String name = "UK_" + uk.getTable().getName();
			checkSize(name);
			uk.setName(name.toLowerCase());
		}
	}

	private static void validate(Configuration configuration) {

		Iterator<PersistentClass> iterator = configuration.getClassMappings();
		while (iterator.hasNext()) {
			PersistentClass clazz = iterator.next();
			Table table = clazz.getTable();
			checkSize(table);
			validateColumns(table);
			validatePrimaryKeys(table);
			validateForeignKeys(table);
			validateUniqueKeys(table);
			validateIndexs(table);
		}

		Iterator<?> iterator2 = configuration.getCollectionMappings();
		while (iterator2.hasNext()) {
			Collection collection = (Collection) iterator2.next();
			Table table = collection.getCollectionTable();
			checkSize(table);
			validateColumns(table);
			validatePrimaryKeys(table);
			validateForeignKeys(table);
			validateIndexs(table);
		}

	}

	@SuppressWarnings("rawtypes")
	private static void validateColumns(Table table) {
		Iterator iterator = table.getColumnIterator();
		while (iterator.hasNext()) {
			Column column = (Column) iterator.next();
			checkSize(column.getName().toLowerCase());
		}
	}

	@SuppressWarnings("rawtypes")
	private static void validateForeignKeys(Table table) {
		Iterator iterator = table.getForeignKeyIterator();
		while (iterator.hasNext()) {
			ForeignKey fk = (ForeignKey) iterator.next();
			checkSize(fk.getName().toLowerCase());
		}
	}

	@SuppressWarnings("rawtypes")
	private static void validateIndexs(Table table) {
		Iterator iterator = table.getIndexIterator();
		while (iterator.hasNext()) {
			Index index = (Index) iterator.next();
			checkSize(index.getName().toLowerCase());
		}
	}

	private static void validatePrimaryKeys(Table table) {
		PrimaryKey primaryKey = table.getPrimaryKey();
		if (primaryKey != null) {
			checkSize(primaryKey.getName().toLowerCase());
		}
	}

	@SuppressWarnings("rawtypes")
	private static void validateUniqueKeys(Table table) {
		Iterator iterator = table.getUniqueKeyIterator();
		while (iterator.hasNext()) {
			UniqueKey uk = (UniqueKey) iterator.next();
			checkSize(uk.getName().toLowerCase());
		}

	}

	@Override
	public void disintegrate(
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {

	}

	@Override
	public void integrate(
			Configuration configuration,
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
		normalize(configuration);
		validate(configuration);
	}

	@Override
	public void integrate(
			MetadataImplementor metadata,
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
	}

}
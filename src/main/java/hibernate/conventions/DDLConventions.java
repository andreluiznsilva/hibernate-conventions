package hibernate.conventions;

import hibernate.conventions.util.ReflectionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;

public class DDLConventions {

	private final Configuration configuration;
	private final ServiceRegistry serviceRegistry;
	private final Dialect dialect;

	public DDLConventions(Configuration configuration, ServiceRegistry serviceRegistry, SessionFactory sessionFactory) {
		this.configuration = configuration;
		this.serviceRegistry = serviceRegistry;
		dialect = (Dialect) ReflectionUtils.getFieldValue("dialect", sessionFactory);
	}

	public List<String> generateCleanScript() {

		List<String> results = new ArrayList<String>();

		String name = dialect.getClass().getSimpleName().toUpperCase();

		if (name.startsWith("HSQL")) {
			results.add("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
		} else if (name.startsWith("POSTGRESQL")) {

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

	public List<String> generateCreateScript() {
		return generate('c');
	}

	public List<String> generateDropScript() {
		return generate('d');
	}

	public List<String> generateUpdateScript() {
		return generate('u');
	}

	private void addAll(String[] origin, List<String> target) {
		target.addAll(Arrays.asList(origin));
	}

	private List<String> generate(char type) {

		List<String> sqls = new ArrayList<String>();

		switch (type) {
			case 'c':
				addAll(configuration.generateSchemaCreationScript(dialect), sqls);
				break;
			case 'd':
				addAll(configuration.generateDropSchemaScript(dialect), sqls);
				break;
			case 'u':
				DatabaseMetadata meta = getData();
				List<SchemaUpdateScript> scripts = configuration.generateSchemaUpdateScriptList(dialect, meta);
				for (SchemaUpdateScript schemaUpdateScript : scripts) {
					sqls.add(schemaUpdateScript.getScript());
				}
				break;
		}

		return sqls;

	}

	private DatabaseMetadata getData() {
		try {
			Connection connection = serviceRegistry.getService(ConnectionProvider.class).getConnection();
			return new DatabaseMetadata(connection, dialect, configuration);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
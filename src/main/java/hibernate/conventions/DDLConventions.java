package hibernate.conventions;

import hibernate.conventions.util.ConventionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
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

	public DDLConventions(Configuration configuration, ServiceRegistry serviceRegistry, Dialect dialect) {
		this.configuration = configuration;
		this.serviceRegistry = serviceRegistry;
		this.dialect = dialect;
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
		return Arrays.asList(configuration.generateSchemaCreationScript(dialect));
	}

	public List<String> generateDropScript() {
		return Arrays.asList(configuration.generateDropSchemaScript(dialect));
	}

	public List<String> generateUpdateScript() {
		List<String> sqls = new ArrayList<String>();
		List<SchemaUpdateScript> scripts = configuration.generateSchemaUpdateScriptList(dialect, getData());
		for (SchemaUpdateScript schemaUpdateScript : scripts) {
			sqls.add(schemaUpdateScript.getScript());
		}
		return sqls;
	}

	private DatabaseMetadata getData() {
		try {
			Connection connection = ConventionUtils.getConnection(serviceRegistry);
			return new DatabaseMetadata(connection, dialect, configuration);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
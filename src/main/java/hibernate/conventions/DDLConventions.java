package hibernate.conventions;

import static hibernate.conventions.utils.ConventionUtils.extractConfiguration;
import static hibernate.conventions.utils.ConventionUtils.extractDialect;
import static hibernate.conventions.utils.ConventionUtils.extractServiceRegistry;
import hibernate.conventions.utils.ConventionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;

public class DDLConventions {

	public static DDLConventions create(EntityManagerFactory entityManagerFactory) {
		return create(entityManagerFactory, extractDialect(entityManagerFactory));
	}

	public static DDLConventions create(EntityManagerFactory entityManagerFactory, Dialect dialect) {
		return new DDLConventions(
		        extractConfiguration(entityManagerFactory), extractServiceRegistry(entityManagerFactory), dialect);
	}

	private final Configuration configuration;
	private final Dialect dialect;
	private final ServiceRegistry serviceRegistry;

	private DDLConventions(Configuration configuration, ServiceRegistry serviceRegistry, Dialect dialect) {
		this.configuration = configuration;
		this.serviceRegistry = serviceRegistry;
		this.dialect = dialect;
	}

	public List<String> generateCleanScript() {

		List<String> results = new ArrayList<String>();

		String name = dialect.getClass().getSimpleName().toLowerCase();

		if (name.startsWith("hsql")) {
			results.add("truncate schema public restart identity and commit no check");
		} else if (name.startsWith("postgresql")) {
			for (String table : listTables()) {
				results.add("truncate " + table + " cascade");
			}
		} else if (name.startsWith("oracle")) {
			for (String table : listTables()) {
				results.add("truncate table " + table);
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

	private List<String> listTables() {

		List<String> results = new ArrayList<String>();

		Iterator<PersistentClass> iterator1 = configuration.getClassMappings();
		while (iterator1.hasNext()) {
			results.add(iterator1.next().getTable().getName());
		}

		Iterator<?> iterator2 = configuration.getCollectionMappings();
		while (iterator2.hasNext()) {
			results.add(((Collection) iterator2.next()).getTable().getName());
		}

		return results;

	}

}
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
import org.hibernate.mapping.Table;
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
	private final ServiceRegistry serviceRegistry;
	private final Dialect dialect;

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

			Iterator<PersistentClass> iterator1 = configuration.getClassMappings();
			while (iterator1.hasNext()) {
				PersistentClass clazz = iterator1.next();
				Table table = clazz.getTable();
				results.add("truncate " + table.getName() + " cascade");
			}

			Iterator<?> iterator2 = configuration.getCollectionMappings();
			while (iterator2.hasNext()) {
				Collection collection = (Collection) iterator2.next();
				Table table = collection.getCollectionTable();
				results.add("truncate " + table.getName() + " cascade");
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
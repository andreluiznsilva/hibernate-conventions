package hibernate.conventions.tools;

import hibernate.conventions.DDLConventions;
import hibernate.conventions.utils.ConventionUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class DDLTools implements Closeable {

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		options.addOption("help", true, "help");
		options.addOption("m", false, "mock database connection (dont need access to the database)");
		options.addOption("e", false, "execute (needs a database connections)");
		options.addOption("d", true, "hibernate dialect");
		options.addOption("p", true, "persistence unit ('default' is default name)");
		options.addOption("o", true, "output file");

		CommandLine cl = new BasicParser().parse(options, args);

		if (cl.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java", options);
		} else {

			boolean mock = cl.hasOption("m");
			boolean execute = cl.hasOption("e");
			String dialect = cl.getOptionValue("d");
			String output = cl.getOptionValue("o");
			String persistence = cl.getOptionValue("p", "default");

			DDLTools tools = null;

			try {

				tools = new DDLTools(output, persistence, dialect, execute, mock);

				for (String arg : cl.getArgs()) {
					tools.doOperation(arg);
				}

			} finally {
				if (tools != null) {
					tools.close();
				}
			}

		}

	}

	private static final String COMMENT = "-- ";
	private static final String END_LINE = "\n";
	private final boolean execute;
	private final String output;
	private final EntityManagerFactory entityManagerFactory;

	private final DDLConventions ddlConventions;

	public DDLTools(String output, String persistence, String dialect, boolean execute, boolean mock) {

		final Map<String, String> config = new HashMap<String, String>();

		if (ConventionUtils.isNotEmpty(dialect)) {
			config.put("hibernate.dialect", dialect);
		}

		if (mock) {
			config.put("hibernate.connection.url", "jdbc:hsqldb:mem:test;shutdown=true;close_result=true");
			config.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		}

		entityManagerFactory = Persistence.createEntityManagerFactory(persistence, config);
		ddlConventions = DDLConventions.create(entityManagerFactory);

		this.execute = execute;
		this.output = output;

		if (ConventionUtils.isNotEmpty(this.output)) {
			cleanOutput(this.output);
		}

	}

	public void close() throws IOException {
		if (entityManagerFactory != null) {
			try {
				entityManagerFactory.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void cleanOutput(String output) {
		File file = new File(output);
		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create output: " + output, e);
		}
	}

	private void doOperation(String operation) {

		List<String> scripts = Collections.emptyList();

		if ("create".equals(operation)) {
			scripts = ddlConventions.generateCreateScript();
		} else if ("drop".equals(operation)) {
			scripts = ddlConventions.generateDropScript();
		} else if ("clean".equals(operation)) {
			scripts = ddlConventions.generateCleanScript();
		} else if ("update".equals(operation)) {
			scripts = ddlConventions.generateUpdateScript();
		} else {
			throw new IllegalArgumentException("No operation found for '" + operation + "'");
		}

		if (ConventionUtils.isNotEmpty(output)) {
			writeOnOutput(operation, scripts);
		}

		if (execute) {
			execute(scripts);
		}

	}

	private void execute(List<String> scripts) {
		EntityManager entityManager = null;
		EntityTransaction transaction = null;
		try {
			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();
			for (String script : scripts) {
				entityManager.createNativeQuery(script).executeUpdate();
			}
			transaction.commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (entityManager != null) {
				if (transaction.isActive()) {
					transaction.rollback();
				}
				try {
					entityManager.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void writeOnOutput(String operation, List<String> scripts) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(output, true);
			writer.write(COMMENT + operation + END_LINE);
			for (String script : scripts) {
				writer.write(script + END_LINE);
			}
			writer.write(END_LINE);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't  write on output:" + output, e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}

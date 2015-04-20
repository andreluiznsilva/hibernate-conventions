package hibernate.conventions.tools;

import hibernate.conventions.DDLConventions;
import hibernate.conventions.utils.ConventionUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class DDLExporterTool implements Closeable {

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		options.addOption("d", true, "hibernate dialect (default: configured at persistence.xml)");
		options.addOption("p", true, "persistence unit (default: persistenceUnit)");
		options.addOption("o", true, "output file (default: target/script/ddl.sql)");

		CommandLine cl = new BasicParser().parse(options, args);

		if (cl.hasOption("h") || args.length == 0) {
			new HelpFormatter().printHelp(
			        "java " + DDLExporterTool.class.getName() + " [operations]",
			        "operations: clean drop create update",
			        options, "");
		} else {

			String dialect = cl.getOptionValue("d");
			String output = cl.getOptionValue("o", "target/script/ddl.sql");
			String persistence = cl.getOptionValue("p", "persistenceUnit");

			String[] operations = cl.getArgs();
			if (operations.length == 0) {
				operations = new String[] { "clean drop create" };
			}

			DDLExporterTool tool = null;
			try {
				tool = new DDLExporterTool(persistence, dialect, output);
				tool.doOperations(cl.getArgs());
			} finally {
				tool.close();
			}

		}

	}

	public void close() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

	private static Map<?, ?> createConfig(String dialect) {
		return ConventionUtils.isNotEmpty(dialect) ?
		        Collections.singletonMap("hibernate.dialect", dialect) :
		        Collections.emptyMap();
	}

	private static final String COMMENT = "-- ";
	private static final String END_LINE = "\n";

	private final String output;
	private final DDLConventions ddlConventions;
	private final EntityManagerFactory entityManagerFactory;

	public DDLExporterTool(EntityManagerFactory entityManagerFactory, String output) {
		this.entityManagerFactory = entityManagerFactory;
		this.ddlConventions = DDLConventions.create(entityManagerFactory);
		this.output = output;
	}

	public DDLExporterTool(String persistence, String dialect, String output) {
		this(Persistence.createEntityManagerFactory(persistence, createConfig(dialect)), output);
	}

	public void doOperations(String... operations) {
		cleanOutput();
		for (String operation : operations) {
			this.doOperation(operation);
		}
	}

	private void cleanOutput() {
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

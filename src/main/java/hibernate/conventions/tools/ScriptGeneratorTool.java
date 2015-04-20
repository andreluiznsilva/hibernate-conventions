package hibernate.conventions.tools;

import hibernate.conventions.DDLConventions;
import hibernate.conventions.utils.ConventionUtils;

import java.io.Closeable;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class ScriptGeneratorTool implements Closeable {

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		options.addOption("d", true, "hibernate dialect (default: configured at persistence.xml)");
		options.addOption("p", true, "persistence unit (default: persistenceUnit)");
		options.addOption("o", true, "output file (default: target/script/ddl.sql)");

		CommandLine cl = new BasicParser().parse(options, args);

		if (cl.hasOption("h") || args.length == 0) {
			new HelpFormatter().printHelp(
			        "java " + ScriptGeneratorTool.class.getName() + " [scripts]",
			        "scripts: clean drop create update",
			        options, "");
		} else {

			String dialect = cl.getOptionValue("d");
			String output = cl.getOptionValue("o", "target/script/ddl.sql");
			String persistence = cl.getOptionValue("p", "persistenceUnit");

			String[] scripts = cl.getArgs();
			if (scripts.length == 0) {
				scripts = new String[] { "clean drop create" };
			}

			ScriptGeneratorTool tool = null;
			try {
				tool = new ScriptGeneratorTool(persistence, dialect, output);
				tool.generate(cl.getArgs());
			} finally {
				tool.close();
			}

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

	public ScriptGeneratorTool(EntityManagerFactory entityManagerFactory, String output) {
		this.entityManagerFactory = entityManagerFactory;
		this.ddlConventions = DDLConventions.create(entityManagerFactory);
		this.output = output;
	}

	public ScriptGeneratorTool(String persistence, String dialect, String output) {
		this(Persistence.createEntityManagerFactory(persistence, createConfig(dialect)), output);
	}

	public void close() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

	public void generate(String... scripts) {

		ConventionUtils.createFile(output);

		for (String script : scripts) {
			this.generateScript(script);
		}

	}

	private void generateScript(String script) {

		List<String> scripts = Collections.emptyList();

		if ("create".equals(script)) {
			scripts = ddlConventions.generateCreateScript();
		} else if ("drop".equals(script)) {
			scripts = ddlConventions.generateDropScript();
		} else if ("clean".equals(script)) {
			scripts = ddlConventions.generateCleanScript();
		} else if ("update".equals(script)) {
			scripts = ddlConventions.generateUpdateScript();
		} else {
			throw new IllegalArgumentException("No script found for '" + script + "'");
		}

		if (ConventionUtils.isNotEmpty(output)) {
			writeOnOutput(script, scripts);
		}

	}

	private void writeOnOutput(String script, List<String> commands) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(output, true);
			writer.write(COMMENT + script + END_LINE);
			for (String command : commands) {
				writer.write(command + END_LINE);
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

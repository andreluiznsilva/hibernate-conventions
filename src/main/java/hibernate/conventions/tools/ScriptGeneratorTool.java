package hibernate.conventions.tools;

import hibernate.conventions.DDLConventions;
import hibernate.conventions.utils.ConventionUtils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class ScriptGeneratorTool {

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

			String[] scripts = cl.getArgs();
			if (scripts.length == 0) {
				scripts = new String[] { "clean drop create" };
			}

			String output = cl.getOptionValue("o", "target/script/ddl.sql");
			FileOutputStream out = ConventionUtils.createFile(output);

			ScriptGeneratorTool tool = null;
			String dialect = cl.getOptionValue("d");
			String persistence = cl.getOptionValue("p", "persistenceUnit");

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistence, createConfig(dialect));

			try {
				tool = new ScriptGeneratorTool(entityManagerFactory);
				tool.generate(out, cl.getArgs());
			} finally {
				ConventionUtils.closeIfNotNull(out);
				entityManagerFactory.close();
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

	private final DDLConventions ddlConventions;

	public ScriptGeneratorTool(EntityManagerFactory entityManagerFactory) {
		this.ddlConventions = DDLConventions.create(entityManagerFactory);
	}

	public void generate(OutputStream output, String... scripts) {

		List<String> lines = new ArrayList<String>();

		for (String script : scripts) {

			lines.add(COMMENT + script);

			if ("create".equals(script)) {
				lines.addAll(ddlConventions.generateCreateScript());
			} else if ("drop".equals(script)) {
				lines.addAll(ddlConventions.generateDropScript());
			} else if ("clean".equals(script)) {
				lines.addAll(ddlConventions.generateCleanScript());
			} else if ("update".equals(script)) {
				lines.addAll(ddlConventions.generateUpdateScript());
			} else {
				throw new IllegalArgumentException("No script found for '" + script + "'");
			}

			lines.add(END_LINE);

		}

		writeOnOutput(output, lines);

	}

	private void writeOnOutput(OutputStream output, List<String> lines) {
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(output);
			for (String command : lines) {
				writer.write(command + END_LINE);
			}
		} catch (Exception e) {
			throw new RuntimeException("Couldn't  write on output:" + output, e);
		} finally {
			ConventionUtils.closeIfNotNull(writer);
		}
	}

}

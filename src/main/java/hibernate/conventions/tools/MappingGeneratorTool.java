package hibernate.conventions.tools;

import hibernate.conventions.utils.ConventionUtils;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.Entity;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class MappingGeneratorTool {

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		options.addOption("o", true, "output file (default: target/classes/META-INF/mapping.xml)");

		CommandLine cl = new BasicParser().parse(options, args);

		if (cl.hasOption("h") || args.length == 0) {
			new HelpFormatter().printHelp(
			        "java " + MappingGeneratorTool.class.getName() + " [packages]",
			        options);
		} else {

			String output = cl.getOptionValue("o", "target/classes/META-INF/mapping.xml");

			new MappingGeneratorTool(output, cl.getArgs()).generate();

		}

	}

	private static List<Class<?>> findClasses(File directory, String packageName) {

		List<Class<?>> classes = new ArrayList<Class<?>>();

		if (!directory.exists()) {
			return classes;
		}

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				String fullName = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				classes.add(loadClass(fullName));
			}
		}

		return classes;

	}

	private static Class<?> loadClass(String fullName) {
		try {
			return Class.forName(fullName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private final String[] packages;
	private final String output;

	public MappingGeneratorTool(String output, String... packages) {
		this.output = output;
		this.packages = packages;
	}

	public void generate() {
		ConventionUtils.createFile(output);
		List<Class<?>> entities = scanEntities();
		writeXml(entities);
	}

	private List<Class<?>> listClasses(String packageName) {

		ArrayList<Class<?>> clazzes = new ArrayList<Class<?>>();

		try {

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;

			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);

			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.toURI()));
			}

			for (File directory : dirs) {
				clazzes.addAll(findClasses(directory, packageName));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return clazzes;

	}

	private List<Class<?>> scanEntities() {

		List<Class<?>> entities = new ArrayList<Class<?>>();

		for (String pack : packages) {

			List<Class<?>> classes = listClasses(pack);

			for (Class<?> clazz : classes) {
				if (clazz.isAnnotationPresent(Entity.class)) {
					entities.add(clazz);
				}
			}

		}

		return entities;

	}

	private void writeXml(List<Class<?>> entities) {

		FileWriter writer = null;

		try {

			writer = new FileWriter(output, false);
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.write("<entity-mappings ");
			writer.write("xmlns=\"http://java.sun.com/xml/ns/persistence/orm\" ");
			writer.write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			writer.write("xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_2_0.xsd\" ");
			writer.write("version=\"2.0\">\n\n");

			for (Class<?> entity : entities) {
				writer.write("	<entity class=\"" + entity.getName() + "\" />\n");
			}

			writer.write("\n</entity-mappings>");

		} catch (Exception e) {
			throw new RuntimeException("Couldn't write on output:" + output, e);
		} finally {
			ConventionUtils.closeIfNotNull(writer);
		}

	}

}

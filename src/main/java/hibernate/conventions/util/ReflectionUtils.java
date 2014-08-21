package hibernate.conventions.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;

public class ReflectionUtils {

	private static final Mirror mirror = new Mirror();

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		return mirror.on(clazz).reflect().constructor().withArgs(parameterTypes);
	}

	public static Field getField(String name, Class<?> clazz) {
		return mirror.on(clazz).reflect().field(name);
	}

	public static Object getFieldValue(Field field, Object target) {
		return mirror.on(target).get().field(field);
	}

	public static Object getFieldValue(String fieldName, Object target) {
		return getFieldValue(getField(fieldName, target.getClass()), target);
	}

	public static Class<?> getGenericTypeAtPosition(Class<?> clazz, Integer index) {
		Class<?> result = null;
		while (clazz != null) {
			try {
				result = mirror.on(clazz).reflect().parentGenericType().atPosition(index);
				break;
			} catch (MirrorException e) {
				clazz = clazz.getSuperclass();
			}
		}
		return result;
	}

	public static Method getMethod(String name, Class<?> clazz, Class<?>... parameterTypes) {
		return mirror.on(clazz).reflect().method(name).withArgs(parameterTypes);
	}

	public static Object getPropertyValue(String propertyName, Object target) {
		return mirror.on(target).invoke().getterFor(propertyName);
	}

	public static Object invoke(Method method, Object object, Object... parameters) {
		return mirror.on(object).invoke().method(method).withArgs(parameters);
	}

	public static Object invoke(String name, Object object, Object... parameters) {
		return mirror.on(object).invoke().method(name).withArgs(parameters);
	}

	public static List<Class<?>> listClasses(String packageName) {

		final ArrayList<Class<?>> clazzes = new ArrayList<Class<?>>();

		try {

			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;

			final String path = packageName.replace('.', '/');
			final Enumeration<URL> resources = classLoader.getResources(path);

			final List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				final URL resource = resources.nextElement();
				dirs.add(new File(resource.toURI()));
			}

			for (final File directory : dirs) {
				clazzes.addAll(findClasses(directory, packageName));
			}

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		return clazzes;

	}

	public static List<Field> listFields(Class<?> clazz) {
		return mirror.on(clazz).reflectAll().fields();
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> loadClass(String name) {
		try {
			return (Class<T>) mirror.reflectClass(name);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T newInstance(Class<T> clazz, Object... parameters) {
		return mirror.on(clazz).invoke().constructor().withArgs(parameters);
	}

	public static void setFieldValue(Field field, Object target, Object value) {
		mirror.on(target).set().field(field).withValue(value);
	}

	public static void setFieldValue(String fieldName, Object target, Object value) {
		Field field = getField(fieldName, target.getClass());
		setFieldValue(field, target, value);
	}

	public static void setPropertyValue(String propertyName, Object target, Object value) {
		mirror.on(target).invoke().setterFor(propertyName).withValue(value);
	}

	private static List<Class<?>> findClasses(File directory, String packageName) {

		final List<Class<?>> classes = new ArrayList<Class<?>>();

		if (!directory.exists()) {
			return classes;
		}

		for (final File file : directory.listFiles()) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				final String fullName = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				classes.add(loadClass(fullName));
			}
		}

		return classes;

	}

	private ReflectionUtils() {
		// contrutor privado para evitar instanciacao
	}

}

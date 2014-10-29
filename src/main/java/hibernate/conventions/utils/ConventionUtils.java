package hibernate.conventions.utils;

import hibernate.conventions.strategy.ConventionNamingStrategy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;

public class ConventionUtils {

	public static Configuration extractConfiguration(EntityManagerFactory entityManagerFactory) {
		ServiceRegistry serviceRegistry = extractServiceRegistry(entityManagerFactory);
		return (Configuration) getFieldValue("configuration", serviceRegistry);
	}

	public static Dialect extractDialect(EntityManagerFactory entityManagerFactory) {
		return (Dialect) getFieldValue("dialect", extractSessionFactory(entityManagerFactory));
	}

	public static ConventionNamingStrategy extractNameStrategy(ObjectNameNormalizer normalizer) {
		return (ConventionNamingStrategy) invokeMethod("getNamingStrategy", normalizer);
	}

	public static ServiceRegistry extractServiceRegistry(EntityManagerFactory entityManagerFactory) {
		return extractServiceRegistry(extractSessionFactory(entityManagerFactory));
	}

	public static Connection getConnection(EntityManagerFactory entityManagerFactory) {
		return getConnection(extractServiceRegistry(entityManagerFactory));
	}

	public static Connection getConnection(ServiceRegistry serviceRegistry) {
		try {
			return serviceRegistry.getService(ConnectionProvider.class).getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	private static ServiceRegistry extractServiceRegistry(SessionFactory sessionFactory) {
		return (ServiceRegistry) getFieldValue("serviceRegistry", sessionFactory);
	}

	private static SessionFactory extractSessionFactory(EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.unwrap(SessionFactory.class);
	}

	private static Object getFieldValue(String fieldName, Object target) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			if (field != null) {
				field.setAccessible(true);
				return field.get(target);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private static Object invokeMethod(String methodName, Object target, Object... parans) {
		try {
			Method method = target.getClass().getDeclaredMethod(methodName);
			if (method != null) {
				method.setAccessible(true);
				return method.invoke(target, parans);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private ConventionUtils() {
	}

}

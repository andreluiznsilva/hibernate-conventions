package hibernate.conventions.util;

import hibernate.conventions.DDLConventions;
import hibernate.conventions.MappingConventions;
import hibernate.conventions.strategy.ConventionNamingStrategy;

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

	public static DDLConventions createDDLConventions(EntityManagerFactory entityManagerFactory) {
		return createDDLConventions(entityManagerFactory, extractDialect(entityManagerFactory));
	}

	public static DDLConventions createDDLConventions(EntityManagerFactory entityManagerFactory, Dialect dialect) {
		return new DDLConventions(extractConfiguration(entityManagerFactory),
				extractServiceRegistry(entityManagerFactory), dialect);
	}

	public static MappingConventions createMappingConventions(EntityManagerFactory entityManagerFactory) {
		return new MappingConventions(extractConfiguration(entityManagerFactory));
	}

	public static Configuration extractConfiguration(EntityManagerFactory entityManagerFactory) {
		ServiceRegistry serviceRegistry = extractServiceRegistry(entityManagerFactory);
		return (Configuration) ReflectionUtils.getFieldValue("configuration", serviceRegistry);
	}

	public static Dialect extractDialect(EntityManagerFactory entityManagerFactory) {
		return (Dialect) ReflectionUtils.getFieldValue("dialect", extractSessionFactory(entityManagerFactory));
	}

	public static ConventionNamingStrategy extractNameStrategy(ObjectNameNormalizer normalizer) {
		return (ConventionNamingStrategy) ReflectionUtils.getPropertyValue("namingStrategy", normalizer);
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
		return (ServiceRegistry) ReflectionUtils.getFieldValue("serviceRegistry", sessionFactory);
	}

	private static SessionFactory extractSessionFactory(EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.unwrap(SessionFactory.class);
	}

	private ConventionUtils() {
	}

}

package hibernate.conventions.util;

import hibernate.conventions.DDLConventions;
import hibernate.conventions.MappingConventions;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
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

	public static ServiceRegistry extractServiceRegistry(EntityManagerFactory entityManagerFactory) {
		return extractServiceRegistry(extractSessionFactory(entityManagerFactory));
	}

	public static Dialect extractDialect(EntityManagerFactory entityManagerFactory) {
		return (Dialect) ReflectionUtils.getFieldValue("dialect", extractSessionFactory(entityManagerFactory));
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

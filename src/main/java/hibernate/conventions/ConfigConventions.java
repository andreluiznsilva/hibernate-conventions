package hibernate.conventions;

import static hibernate.conventions.utils.ConventionUtils.extractConfiguration;
import hibernate.conventions.utils.ConventionUtils;

import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.Configuration;

public class ConfigConventions {

	public static ConfigConventions create(Configuration configuration) {
		return new ConfigConventions(configuration);
	}

	public static ConfigConventions create(EntityManagerFactory entityManagerFactory) {
		return create(extractConfiguration(entityManagerFactory));
	}

	private final Configuration configuration;

	private ConfigConventions(Configuration configuration) {
		this.configuration = configuration;
	}

	public void validate() {

		boolean checkConfiguration = ConventionUtils.getProperty(configuration, "hibernate.conventions.checkConfig", true);

		if (checkConfiguration) {

			boolean autoCommit = ConventionUtils.getProperty(configuration, "hibernate.connection.autocommit", true);

			if (autoCommit) {
				throw new IllegalArgumentException(
				        "AutoCommit should be disable. Set hibernate.connection.autocommit=false on config file, "
				                + " or disable this check using hibernate.conventions.checkConfig=false");
			}

		}

	}

}

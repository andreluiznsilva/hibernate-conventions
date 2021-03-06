package hibernate.conventions.integrator;

import hibernate.conventions.ConfigConventions;
import hibernate.conventions.MappingConventions;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class ConventionsIntegrator implements Integrator {

	public void disintegrate(
	        SessionFactoryImplementor sessionFactory,
	        SessionFactoryServiceRegistry serviceRegistry) {

	}

	public void integrate(
	        Configuration configuration,
	        SessionFactoryImplementor sessionFactory,
	        SessionFactoryServiceRegistry serviceRegistry) {

		ConfigConventions configConventions = ConfigConventions.create(configuration);
		configConventions.validate();

		MappingConventions mappingConventions = MappingConventions.create(configuration);
		mappingConventions.normalize();
		mappingConventions.validate();

	}

	public void integrate(
	        MetadataImplementor metadata,
	        SessionFactoryImplementor sessionFactory,
	        SessionFactoryServiceRegistry serviceRegistry) {
	}

}
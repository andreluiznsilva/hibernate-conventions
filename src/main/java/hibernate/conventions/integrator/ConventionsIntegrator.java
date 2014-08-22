package hibernate.conventions.integrator;

import hibernate.conventions.HibernateConventions;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class ConventionsIntegrator implements Integrator {

	@Override
	public void disintegrate(
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {

	}

	@Override
	public void integrate(
			Configuration configuration,
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {

		HibernateConventions conventions = new HibernateConventions(configuration);
		conventions.normalize();
		conventions.validate();

	}

	@Override
	public void integrate(
			MetadataImplementor metadata,
			SessionFactoryImplementor sessionFactory,
			SessionFactoryServiceRegistry serviceRegistry) {
	}

}
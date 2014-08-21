package hibernate.conventions.generator;

import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

public class TableSequenceGenerator extends SequenceGenerator {

	@Override
	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
		String sequence = params.getProperty(SEQUENCE);
		if (sequence == null || sequence.isEmpty()) {
			String table = params.getProperty(TABLE);
			if (table != null && !table.isEmpty()) {
				sequence = "seq_" + table;
				params.setProperty(SEQUENCE, sequence);
			}
		}
		super.configure(type, params, dialect);
	}

}
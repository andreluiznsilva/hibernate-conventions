package hibernate.conventions.generator;

import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

public class TableSequenceGenerator extends SequenceGenerator {

	@Override
	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
		if (params.getProperty(SEQUENCE) == null || params.getProperty(SEQUENCE).length() == 0) {
			String tableName = params.getProperty(TABLE);
			if (tableName != null) {
				String seqName = "seq_" + tableName + "";
				params.setProperty(SEQUENCE, seqName);
			}
		}
		super.configure(type, params, dialect);
	}

}
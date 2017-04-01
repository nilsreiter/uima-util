package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP;

public class PrimitiveColumn extends Column {
	Feature feature;

	public PrimitiveColumn(Feature feature) {
		super(new String[] { feature.getShortName() });
		this.datatype = Datatype.fromString(feature.getRange().getName());
		this.feature = feature;
	}

	@Override
	public Object getValue(TOP a) {
		switch (getDatatype()) {
		case BOOLEAN:
			return a.getBooleanValue(feature);
		case DOUBLE:
			return a.getDoubleValue(feature);
		case FLOAT:
			return a.getFloatValue(feature);
		case INTEGER:
			return a.getIntValue(feature);
		case STRING:
			return a.getFeatureValueAsString(feature);
		case LONG:
			return a.getLongValue(feature);
		default:
			return null;
		}
	}

	@Override
	public boolean isMultiplying() {
		return false;
	}

}

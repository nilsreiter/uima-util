package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.cas.FeaturePath;
import org.apache.uima.jcas.tcas.Annotation;

public class FeaturePathColumn extends Column {

	FeaturePath featurePath;

	public FeaturePathColumn(FeaturePath fp) {
		super(new String[] { fp.getFeaturePath() });
		datatype = Datatype.STRING;
		featurePath = fp;
	}

	public FeaturePathColumn(FeaturePath fp, String label) {
		super(new String[] { label });
		datatype = Datatype.STRING;
		featurePath = fp;
	}

	@Override
	public Object getValue(Annotation a) {
		switch (getDatatype()) {
		case BOOLEAN:
			return featurePath.getBooleanValue(a);

		case DOUBLE:
			return featurePath.getDoubleValue(a);
		case FLOAT:
			return featurePath.getFloatValue(a);
		case INTEGER:
			return featurePath.getIntValue(a);
		case STRING:
			return featurePath.getValueAsString(a);
		default:
			return null;
		}
	}

	@Override
	public boolean isMultiplying() {
		return false;
	}

}

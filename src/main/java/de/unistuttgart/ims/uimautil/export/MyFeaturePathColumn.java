package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

public class MyFeaturePathColumn extends Column {

	String featurePath;
	String[] pathParts;

	public MyFeaturePathColumn(String fp) {
		super(new String[] { fp });
		datatype = Datatype.STRING;
		featurePath = fp;
		if (featurePath.startsWith("/"))
			featurePath = featurePath.substring(1);
		pathParts = featurePath.split("/");
	}

	public MyFeaturePathColumn(String fp, String label) {
		super(new String[] { label });
		datatype = Datatype.STRING;
		featurePath = fp;
		if (featurePath.startsWith("/"))
			featurePath = featurePath.substring(1);
		pathParts = featurePath.split("/");
	}

	@Override
	public Object getValue(TOP a) {
		return apply(a, pathParts);
	}

	@Override
	public boolean isMultiplying() {
		return false;
	}

	private Object getPrimitiveFunction(FeatureStructure fs, String function) {
		if (function.equalsIgnoreCase("coveredText()")) {
			return ((Annotation) fs).getCoveredText();
		}
		return null;
	}

	private Object getPrimitive(FeatureStructure fs, Feature feature) {
		String s = feature.getRange().getName();
		if (s.equals("uima.cas.String"))
			return fs.getStringValue(feature);
		else if (s.equals("uima.cas.Integer"))
			return fs.getIntValue(feature);
		else if (s.equals("uima.cas.Double"))
			return fs.getDoubleValue(feature);
		return null;
	}

	private Object getPrimitiveComponent(FeatureStructure array, int index) {
		Type cType = array.getType().getComponentType();
		if (cType.getName().equals("uima.cas.String"))
			return ((StringArray) array).get(index);
		else if (cType.getName().equals("uima.cas.Double"))
			return ((DoubleArray) array).get(index);
		else if (cType.getName().equals("uima.cas.Integer"))
			return ((IntegerArray) array).get(index);
		return null;
	}

	private Object apply(TOP a, String[] path) {
		FeatureStructure current = a;
		for (int i = 0; i < path.length; i++) {
			if (current == null)
				return null;
			String featureName = path[i];

			if (featureName.contains("(")) {
				return getPrimitiveFunction(current, featureName);
			} else if (current.getType().isArray()) {
				int arrayIndex = Integer.valueOf(featureName);
				if (current.getType().getComponentType().isPrimitive()) {
					return getPrimitiveComponent(current, arrayIndex);
				} else {
					FSArray arr = (FSArray) current;
					current = arr.get(arrayIndex);
				}
			} else {
				Feature feature = current.getType().getFeatureByBaseName(featureName);
				if (feature.getRange().isPrimitive()) {
					return getPrimitive(current, feature);
				} else {
					current = current.getFeatureValue(feature);
				}
			}
		}
		return null;
	}

}

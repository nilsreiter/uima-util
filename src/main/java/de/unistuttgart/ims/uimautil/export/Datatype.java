package de.unistuttgart.ims.uimautil.export;

public enum Datatype {
	STRING, DOUBLE, INTEGER, FLOAT, BOOLEAN, OTHER, LONG;

	public static Datatype fromString(String s) {
		if (s.equals("uima.cas.String"))
			return STRING;
		if (s.equals("uima.cas.Double"))
			return DOUBLE;
		if (s.equals("uima.cas.Float"))
			return FLOAT;
		if (s.equals("uima.cas.Boolean"))
			return BOOLEAN;
		if (s.equals("uima.cas.Long"))
			return LONG;
		if (s.equals("uima.cas.Integer"))
			return INTEGER;
		return OTHER;
	}
}

package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.jcas.tcas.Annotation;

public abstract class Column {
	Datatype datatype;
	String[] label;

	public Column(String[] l) {
		label = l;
	}

	public String[] getLabel() {
		return label;
	}

	public void setLabel(String[] label) {
		this.label = label;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public abstract Object getValue(Annotation a);

	public abstract boolean isMultiplying();
}

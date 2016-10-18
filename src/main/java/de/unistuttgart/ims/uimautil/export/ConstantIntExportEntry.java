package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.jcas.tcas.Annotation;

public class ConstantIntExportEntry extends ExportEntry {

	int dramaLength;

	public ConstantIntExportEntry(int l) {
		super(new String[] { "length" });
		dramaLength = l;

	}

	@Override
	public Object getValue(Annotation a) {
		return dramaLength;
	}

	@Override
	public boolean isMultiplying() {
		return false;
	}

}

package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.jcas.tcas.Annotation;

@Deprecated
public class ConstantIntExportEntry extends ExportEntry {

	int dramaLength;

	public ConstantIntExportEntry(String label, int l) {
		super(new String[] { label });
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

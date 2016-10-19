package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.jcas.tcas.Annotation;

@Deprecated
public class ConstantStringExportEntry extends ExportEntry {

	String dramaId;

	public ConstantStringExportEntry(String label, String content) {
		super(new String[] { label });
		dramaId = content;

	}

	@Override
	public Object getValue(Annotation a) {
		return dramaId;
	}

	@Override
	public boolean isMultiplying() {
		return false;
	}

}

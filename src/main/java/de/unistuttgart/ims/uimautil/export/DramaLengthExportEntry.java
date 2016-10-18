package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.jcas.tcas.Annotation;

public class DramaLengthExportEntry extends ExportEntry {

	int dramaLength;

	public DramaLengthExportEntry(int l) {
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

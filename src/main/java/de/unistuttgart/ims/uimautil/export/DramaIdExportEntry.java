package de.unistuttgart.ims.uimautil.export;

import org.apache.uima.jcas.tcas.Annotation;

public class DramaIdExportEntry extends ExportEntry {

	String dramaId;

	public DramaIdExportEntry(String did) {
		super(new String[] { "drama" });
		dramaId = did;

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

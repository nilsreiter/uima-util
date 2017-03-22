package de.unistuttgart.ims.uimautil;

import java.util.Comparator;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationComparator implements Comparator<Annotation> {

	boolean useEnd = false;

	public AnnotationComparator() {
	}

	public AnnotationComparator(boolean useEnd) {
		this.useEnd = useEnd;
	}

	public int compare(Annotation o1, Annotation o2) {
		if (useEnd)
			return Integer.compare(o1.getEnd(), o2.getEnd());
		return Integer.compare(o1.getBegin(), o2.getBegin());
	}

}

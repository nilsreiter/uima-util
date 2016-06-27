package de.unistuttgart.ims.uimautil;

import java.util.LinkedList;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class FixedNumberWindowAnnotator extends AbstractWindowAnnotator {

	public static final String PARAM_NUMBER_WINDOWS = "Number of Windows";

	@ConfigurationParameter(name = PARAM_NUMBER_WINDOWS, defaultValue = "10", mandatory = false)
	int numberOfWindows = 10;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		LinkedList<? extends Annotation> bases = new LinkedList<Annotation>(JCasUtil.select(jcas, baseAnnotation));
		int n = bases.size();
		double avgLength = (double) n / (double) numberOfWindows;
		int c = (int) Math.ceil(avgLength);
		int last = 0;
		int windows = 0;
		for (int i = 0; i < n; i++) {
			if (i % c == 0 && windows < numberOfWindows - 1) {
				AnnotationFactory.createAnnotation(jcas, bases.get(last + 1).getBegin(), bases.get(i).getEnd(),
						targetAnnotation);
				windows++;
				last = i;
			}
		}
		AnnotationFactory.createAnnotation(jcas, bases.get(last).getBegin(), bases.getLast().getEnd(),
				targetAnnotation);

	}

}

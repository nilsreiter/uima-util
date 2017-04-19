package de.unistuttgart.ims.uimautil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public class TrimAnnotations extends JCasAnnotator_ImplBase {
	public static final String PARAM_TYPE = "Type to trim";

	@ConfigurationParameter(name = PARAM_TYPE)
	String type = null;
	Class<? extends Annotation> clazz;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		clazz = TypeParameterUtil.getClass(type);
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		AnnotationUtil.trim(JCasUtil.select(aJCas, clazz));
	}

}

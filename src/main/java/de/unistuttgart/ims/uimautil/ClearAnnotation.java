package de.unistuttgart.ims.uimautil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This component removes all annotations of a given type.
 *
 * @author reiterns
 * @since 0.2
 */
public class ClearAnnotation extends JCasAnnotator_ImplBase {

	public static final String PARAM_TYPE = "Type to Remove";

	@ConfigurationParameter(name = PARAM_TYPE)
	String type = null;
	Class<? extends Annotation> clazz;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		Class<?> cl;
		try {
			cl = Class.forName(type);
		} catch (final ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}

		clazz = (Class<? extends Annotation>) cl;

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			Annotation a = clazz.getConstructor(JCas.class).newInstance(jcas);
			jcas.removeAllIncludingSubtypes(a.getTypeIndexID());
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}

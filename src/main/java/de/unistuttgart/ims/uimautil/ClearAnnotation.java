package de.unistuttgart.ims.uimautil;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
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
		// TODO: new implementation based on jcas.removeAllIncludingSubtypes(i);
		final List<Annotation> annList = new LinkedList<Annotation>(JCasUtil.select(jcas, clazz));
		for (final Annotation anno : annList) {
			anno.removeFromIndexes();
		}

	}

}

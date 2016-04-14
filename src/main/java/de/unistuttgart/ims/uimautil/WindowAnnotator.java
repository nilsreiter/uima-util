package de.unistuttgart.ims.uimautil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This component adds window annotations of type PARAM_TARGET_ANNOTATION over
 * PARAM_WINDOW_SIZE base annotations.
 *
 * @author reiterns
 * @since 0.4.3
 *
 */
public class WindowAnnotator extends JCasAnnotator_ImplBase {
	public static final String PARAM_BASE_ANNOTATION = "Base Annotation";
	public static final String PARAM_TARGET_ANNOTATION = "Target Annotation";
	public static final String PARAM_WINDOW_SIZE = "Window Size";
	public static final String PARAM_OVERLAPS = "Overlapping Windows";

	@ConfigurationParameter(name = PARAM_BASE_ANNOTATION, mandatory = false)
	String baseAnnotationClassName = null;

	@ConfigurationParameter(name = PARAM_TARGET_ANNOTATION, mandatory = true)
	String targetAnnotationClassName = null;

	@ConfigurationParameter(name = PARAM_WINDOW_SIZE, mandatory = true)
	int windowSize;

	@ConfigurationParameter(name = PARAM_OVERLAPS, mandatory = false, defaultValue = "true")
	boolean overlappingWindows = false;

	Class<? extends Annotation> targetAnnotation = null;

	Class<? extends Annotation> baseAnnotation = null;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		Class<?> tA;

		try {
			tA = Class.forName(targetAnnotationClassName);
			if (Annotation.class.isAssignableFrom(tA))
				targetAnnotation = (Class<? extends Annotation>) tA;
			else
				throw new ResourceInitializationException();
		} catch (final ClassNotFoundException e1) {
			throw new ResourceInitializationException(e1);
		}

		try {
			if (baseAnnotationClassName != null) {
				tA = Class.forName(baseAnnotationClassName);
				if (Annotation.class.isAssignableFrom(tA))
					baseAnnotation = (Class<? extends Annotation>) tA;
				else
					throw new ResourceInitializationException();
			}
		} catch (final ClassNotFoundException e1) {
			throw new ResourceInitializationException(e1);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		int i = 0;
		int b = 0;
		for (final Annotation anno : JCasUtil.select(jcas, baseAnnotation)) {
			i++;
			if ((i % windowSize) == 0) {
				AnnotationFactory.createAnnotation(jcas, b, anno.getEnd(), targetAnnotation);
			}
			if ((i % windowSize) == 1) {
				b = anno.getBegin();
			}
		}
		AnnotationFactory.createAnnotation(jcas, b, jcas.getDocumentText().length(), targetAnnotation);
	}

}

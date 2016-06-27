package de.unistuttgart.ims.uimautil;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This component adds window annotations of type PARAM_TARGET_ANNOTATION over
 * PARAM_WINDOW_SIZE base annotations. The last window might be shorter than the
 * specified window size. By default, the component generates non-overlapping
 * windows. If the parameter PARAM_OVERLAPS is set to true, overlapping windows
 * will be annotated. This option also ensures that every window is exactly the
 * right amount of sub annotations long.
 *
 * @author reiterns
 * @since 0.4.3
 *
 */
public class WindowAnnotator extends AbstractWindowAnnotator {
	public static final String PARAM_WINDOW_SIZE = "Window Size";
	public static final String PARAM_OVERLAPS = "Overlapping Windows";
	public static final String PARAM_SEGMENT_ANNOTATION = "Segment Annotation";

	@ConfigurationParameter(name = PARAM_SEGMENT_ANNOTATION, mandatory = false)
	String segmentAnnotationClassName = null;

	@ConfigurationParameter(name = PARAM_WINDOW_SIZE, mandatory = true)
	int windowSize;

	@ConfigurationParameter(name = PARAM_OVERLAPS, mandatory = false, defaultValue = "false")
	boolean overlappingWindows = false;

	Class<? extends Annotation> segmentAnnotationClass = null;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		Class<?> tA;

		try {
			if (segmentAnnotationClassName != null) {
				tA = Class.forName(segmentAnnotationClassName);
				if (Annotation.class.isAssignableFrom(tA))
					segmentAnnotationClass = (Class<? extends Annotation>) tA;
				else
					throw new ResourceInitializationException();
			}
		} catch (final ClassNotFoundException e1) {
			throw new ResourceInitializationException(e1);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		if (segmentAnnotationClass != null) {
			for (final Annotation segAnno : JCasUtil.select(jcas, segmentAnnotationClass)) {
				doAnnotations(jcas, JCasUtil.selectCovered(jcas, baseAnnotation, segAnno), segAnno.getEnd());

			}
		} else {
			doAnnotations(jcas, JCasUtil.select(jcas, baseAnnotation), jcas.getDocumentText().length());
		}
	}

	protected void doAnnotations(JCas jcas, Collection<? extends Annotation> baseAnnotations, int end) {
		int i = 0;
		int b = 0;

		LinkedList<Annotation> window = null;
		if (overlappingWindows) {
			window = new LinkedList<Annotation>();
		}
		for (final Annotation anno : baseAnnotations) {
			if (overlappingWindows) {
				window.add(anno);
				if (window.size() >= windowSize) {
					final Annotation firstAnno = window.pop();
					AnnotationFactory.createAnnotation(jcas, firstAnno.getBegin(), anno.getEnd(), targetAnnotation);
				}
			} else {
				i++;
				if ((i % windowSize) == 0) {
					AnnotationFactory.createAnnotation(jcas, b, anno.getEnd(), targetAnnotation);
				}
				if ((i % windowSize) == 1) {
					b = anno.getBegin();
				}
			}
		}
		if (!overlappingWindows)
			AnnotationFactory.createAnnotation(jcas, b, end, targetAnnotation);

	}

}

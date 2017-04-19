package de.unistuttgart.ims.uimautil;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This annotator searches for annotations of type { @link
 * AbstractWindowAnnotator#PARAM_BASE_ANNOTATION }. For such annotations, the
 * annotator creates annotations of type { @link
 * AbstractWindowAnnotator#PARAM_TARGET_ANNOTATION } that cover a context of
 * {@link #PARAM_CONTEXT_SIZE_LEFT} and {@link #PARAM_CONTEXT_SIZE_RIGHT} of
 * {@link #PARAM_CONTEXT_CLASS} annotations.
 * 
 * E.g., it can be used to get context annotations for a number of sentences
 * around some annotated tokens.
 * 
 * @author reiterns
 *
 */
public class ContextWindowAnnotator extends AbstractWindowAnnotator {

	public static final String PARAM_CONTEXT_CLASS = "Context class";
	public static final String PARAM_CONTEXT_SIZE_LEFT = "Left context size";
	public static final String PARAM_CONTEXT_SIZE_RIGHT = "Right context size";

	@ConfigurationParameter(name = PARAM_CONTEXT_CLASS, defaultValue = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence")
	String contextClassName;
	Class<? extends Annotation> contextClass;

	@ConfigurationParameter(name = PARAM_CONTEXT_SIZE_LEFT, mandatory = false, defaultValue = "3")
	int leftContextSize = 3;

	@ConfigurationParameter(name = PARAM_CONTEXT_SIZE_RIGHT, mandatory = false, defaultValue = "3")
	int rightContextSize = 3;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		contextClass = TypeParameterUtil.getClass(contextClassName);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		SortedMap<Annotation, Boolean> map = new TreeMap<Annotation, Boolean>(new Comparator<Annotation>() {

			public int compare(Annotation o1, Annotation o2) {
				return Integer.compare(o1.getBegin(), o2.getBegin());
			}

		});
		for (Annotation s : JCasUtil.select(jcas, contextClass)) {
			map.put(s, false);
		}
		for (Annotation s : JCasUtil.select(jcas, contextClass)) {
			if (JCasUtil.selectCovered(baseAnnotation, s).size() > 0) {
				getLogger().debug("Contexting sentence " + s);
				map.put(s, true);
				for (Annotation s1 : JCasUtil.selectPreceding(contextClass, s, leftContextSize)) {
					getLogger().debug("Contexting sentence " + s1);
					map.put(s1, true);
				}
				for (Annotation s2 : JCasUtil.selectFollowing(contextClass, s, rightContextSize)) {
					getLogger().debug("Contexting sentence " + s2);
					map.put(s2, true);
				}
			}
		}

		int begin = -1;
		Annotation lastSentence = null;
		for (Annotation s : map.keySet()) {
			if (map.get(s) && begin < 0) {
				begin = s.getBegin();
			} else if (!map.get(s) && begin >= 0) {
				AnnotationFactory.createAnnotation(jcas, begin, lastSentence.getEnd(), targetAnnotation);
				begin = -1;
			}
			lastSentence = s;
		}
	}

}

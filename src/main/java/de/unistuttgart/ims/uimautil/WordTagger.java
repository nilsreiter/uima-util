package de.unistuttgart.ims.uimautil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * This UIMA components tags every occurrence of one of the words provided in a
 * list. Can be used to tag semantically grouped words (e.g., temporal adverbs).
 *
 *
 * @author reiterns
 * @since 0.4.2
 */
public class WordTagger extends JCasAnnotator_ImplBase {

	public static final String PARAM_WORD = "Word";
	public static final String PARAM_BASE_ANNOTATION = "Base Annotation";
	public static final String PARAM_TARGET_ANNOTATION = "Target Annotation";
	public static final String PARAM_CI = "Casing";

	@ConfigurationParameter(name = PARAM_WORD, mandatory = true)
	String word;

	@ConfigurationParameter(name = PARAM_BASE_ANNOTATION, mandatory = false)
	String baseAnnotationClassName = null;

	@ConfigurationParameter(name = PARAM_TARGET_ANNOTATION, mandatory = true)
	String targetAnnotationClassName = null;

	@ConfigurationParameter(name = PARAM_CI, mandatory = false, defaultValue = "false")
	boolean caseIndependent = false;

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
		if (baseAnnotation != null) {

			for (final Annotation anno : JCasUtil.select(jcas, baseAnnotation)) {
				if ((caseIndependent && anno.getCoveredText().equalsIgnoreCase(word))
						|| anno.getCoveredText().equals(word)) {
					AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(), targetAnnotation);
				}
			}

		} else {

			final Pattern pattern = Pattern.compile("\\b" + word + "\\b",
					Pattern.UNICODE_CASE | (caseIndependent ? Pattern.CASE_INSENSITIVE : 0));
			final Matcher matcher = pattern.matcher(jcas.getDocumentText());
			while (matcher.find()) {
				AnnotationFactory.createAnnotation(jcas, matcher.start(), matcher.end(), targetAnnotation);

			}
		}
	}
}

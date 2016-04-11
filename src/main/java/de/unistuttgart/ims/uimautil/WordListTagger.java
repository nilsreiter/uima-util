package de.unistuttgart.ims.uimautil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.ExternalResourceAware;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

public class WordListTagger extends JCasAnnotator_ImplBase {

	public static final String RESOURCE_WORDLIST = "Word List";
	public static final String PARAM_BASE_ANNOTATION = "Base Annotation";
	public static final String PARAM_TARGET_ANNOTATION = "Target Annotation";
	public static final String PARAM_CI = "Casing";

	@ExternalResource(key = RESOURCE_WORDLIST, mandatory = true)
	WordList wordList;

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
				if (wordList.contains(anno.getCoveredText(), caseIndependent))
					AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(), targetAnnotation);
			}

		} else {
			// TODO: not implemented, should tag every substring
		}
	}

	public static class WordList implements SharedResourceObject, ExternalResourceAware {

		@ConfigurationParameter(name = ExternalResourceFactory.PARAM_RESOURCE_NAME)
		private String resourceName;

		public String getResourceName() {
			return resourceName;
		}

		HashSet<String> words;
		HashSet<String> lowerWords;

		public boolean contains(String s, boolean ci) {
			if (ci) {
				return lowerWords.contains(s.toLowerCase());
			} else
				return words.contains(s);
		}

		public void load(DataResource aData) throws ResourceInitializationException {
			try {
				loadFromStream(aData.getInputStream());
			} catch (final IOException e) {
				throw new ResourceInitializationException(e);
			}
		}

		public void loadFromStream(InputStream is) throws IOException {
			words = new HashSet<String>(IOUtils.readLines(is, "UTF-8"));
			lowerWords = new HashSet<String>();
			for (final String s : words)
				lowerWords.add(s.toLowerCase());
		}

		public void afterResourcesInitialized() throws ResourceInitializationException {
			// TODO Auto-generated method stub

		}
	}

}

package de.unistuttgart.ims.uimautil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;

/**
 * This UIMA components tags every occurrence of one of the words provided in a
 * list. Can be used to tag semantically grouped words (e.g., temporal adverbs).
 *
 *
 * @author reiterns
 * @since 0.4.2
 */
public class WordListTagger extends SimpleTagger {

	public static final String RESOURCE_WORDLIST = "Word List";
	@ExternalResource(key = RESOURCE_WORDLIST, mandatory = true)
	WordList wordList;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		WordListDescription desc = AnnotationFactory.createAnnotation(jcas, 0, 0, WordListDescription.class);
		desc.setLength(wordList.getLength(caseIndependent));
		desc.setName(wordList.listName);
		Feature feature = null;
		if (targetFeatureName != null)
			feature = jcas.getTypeSystem().getType(targetAnnotationClassName).getFeatureByBaseName(targetFeatureName);
		if (baseAnnotation != null) {

			for (final Annotation anno : JCasUtil.select(jcas, baseAnnotation)) {
				String s;
				if (listContainsLemmas) {
					try {
						s = JCasUtil.selectCovered(Lemma.class, anno).get(0).getCoveredText();
					} catch (Exception e) {
						s = anno.getCoveredText();
					}
				} else {
					s = anno.getCoveredText();
				}
				if (wordList.contains(s, caseIndependent)) {
					final Annotation newAnno = AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(),
							targetAnnotation);
					if (feature != null)
						newAnno.setFeatureValueFromString(feature, wordList.listName);
				}
			}

		} else {

			final Set<String> words = (caseIndependent ? wordList.lowerWords : wordList.words);
			for (final String s : words) {
				final Pattern pattern = Pattern.compile("\\b" + s + "\\b",
						Pattern.UNICODE_CASE | (caseIndependent ? Pattern.CASE_INSENSITIVE : 0));
				final Matcher matcher = pattern.matcher(jcas.getDocumentText());
				while (matcher.find()) {
					final Annotation newAnno = AnnotationFactory.createAnnotation(jcas, matcher.start(), matcher.end(),
							targetAnnotation);
					if (feature != null)
						newAnno.setFeatureValueFromString(feature, wordList.listName);

				}
			}
		}
	}

	/**
	 * Represents the word list.
	 *
	 * @author reiterns
	 *
	 */
	public static class WordList extends Resource_ImplBase {

		public static final String PARAM_SOURCE_URL = "Word List URL";
		public static final String PARAM_LIST_NAME = "Word List Name";

		@ConfigurationParameter(name = PARAM_SOURCE_URL, mandatory = true)
		String resourceURL = null;

		@ConfigurationParameter(name = PARAM_LIST_NAME, mandatory = false)
		String listName = null;

		@Override
		public boolean initialize(final ResourceSpecifier aSpecifier, final Map<String, Object> aAdditionalParams)
				throws ResourceInitializationException {

			super.initialize(aSpecifier, aAdditionalParams);
			try {
				loadFromStream(new URL(resourceURL).openStream());
			} catch (final MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (final IOException e) {
				e.printStackTrace();
				return false;
			}
			if (listName == null)
				listName = resourceURL;
			return true;
		}

		HashSet<String> words;
		HashSet<String> lowerWords;

		public boolean contains(String s, boolean ci) {
			if (ci) {
				return lowerWords.contains(s.toLowerCase());
			} else
				return words.contains(s);
		}

		public void loadFromStream(InputStream is) throws IOException {
			words = new HashSet<String>(IOUtils.readLines(is, "UTF-8"));
			lowerWords = new HashSet<String>();
			for (final String s : words)
				lowerWords.add(s.toLowerCase());
		}

		@Override
		public void afterResourcesInitialized() throws ResourceInitializationException {

		}

		public int getLength(boolean ci) {
			if (ci)
				return lowerWords.size();
			else
				return lowerWords.size();
		}
	}

}

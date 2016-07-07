package de.unistuttgart.ims.uimautil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
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
import de.unistuttgart.ims.uimautil.trie.Trie;

public class NGramTagger extends SimpleTagger {
	public static final String RESOURCE_WORDLIST = "N-Gram List";

	@ExternalResource(key = RESOURCE_WORDLIST, mandatory = true)
	NGramList wordList;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		WordListDescription desc = AnnotationFactory.createAnnotation(jcas, 0, 0, WordListDescription.class);
		desc.setLength(wordList.getLength(caseIndependent));
		desc.setName(wordList.listName);

		Feature feature = null;
		if (targetFeatureName != null)
			feature = jcas.getTypeSystem().getType(targetAnnotationClassName).getFeatureByBaseName(targetFeatureName);

		// create lemma index
		Map<String, List<Lemma>> lemmaIndex = new HashMap<String, List<Lemma>>();
		for (Lemma lemma : JCasUtil.select(jcas, Lemma.class)) {
			if (!lemmaIndex.containsKey(lemma.getValue()))
				lemmaIndex.put(lemma.getValue(), new LinkedList<Lemma>());
			lemmaIndex.get(lemma.getValue()).add(lemma);
		}

		for (String[] entry : wordList.getEntries()) {
			if (lemmaIndex.containsKey(entry[0])) {
				for (Lemma lemma : lemmaIndex.get(entry[0])) {
					int begin = lemma.getBegin();
					int end = lemma.getEnd();
					Lemma nextLemma;
					for (int i = 1; i < entry.length; i++) {
						nextLemma = JCasUtil.selectFollowing(Lemma.class, lemma, 1).get(0);
						if (nextLemma.getValue().equalsIgnoreCase(entry[i])) {
							end = nextLemma.getEnd();
						} else {
							end = -1;
						}
					}
					if (end >= 0) {
						Annotation newAnno = AnnotationFactory.createAnnotation(jcas, begin, end, targetAnnotation);
						if (feature != null)
							newAnno.setFeatureValueFromString(feature, wordList.listName);
					}
				}
			}
		}

		Set<Annotation> toRemove = new HashSet<Annotation>();
		for (Annotation a : JCasUtil.select(jcas, targetAnnotation)) {
			toRemove.addAll(JCasUtil.selectCovered(targetAnnotation, a));
		}

		for (Annotation a : toRemove)
			a.removeFromIndexes();
	}

	public static class NGramList extends Resource_ImplBase {
		public static final String PARAM_SOURCE_URL = "NGram List URL";
		public static final String PARAM_LIST_NAME = "NGram List Name";

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

		Trie<String> ngrams = new Trie<String>();
		List<String[]> entries = new LinkedList<String[]>();

		public List<String[]> getEntries() {
			return entries;
		}

		int lines = 0;

		public void loadFromStream(InputStream is) throws IOException {
			for (String line : IOUtils.readLines(is, "UTF-8")) {
				ngrams.addWord(Arrays.asList(line.split("[ \t]")));

				entries.add(line.split("[ \t]"));

				lines++;
			}

		}

		@Override
		public void afterResourcesInitialized() throws ResourceInitializationException {

		}

		public Trie<String> getTrie() {
			return ngrams;
		}

		public int getLength(boolean ci) {
			return lines;
		}
	}
}
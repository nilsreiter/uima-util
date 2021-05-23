package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.stanfordnlp.StanfordLemmatizer;
import org.dkpro.core.tokit.BreakIteratorSegmenter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.unistuttgart.ims.uimautil.api.TestType;

public class TestNGramTaggerLong {
	JCas jcas;

	@Before
	public void setUp() throws ResourceInitializationException {
		JCasIterable iterable = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(TextReader.class, TextReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/war-and-peace.txt", TextReader.PARAM_LANGUAGE, "en"),
				AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class),
				AnalysisEngineFactory.createEngineDescription(StanfordLemmatizer.class));
		jcas = iterable.iterator().next();
	}

	@Ignore
	@Test
	public void testLongText() throws ResourceInitializationException, UIMAException, IOException {

		final ExternalResourceDescription erd = ExternalResourceFactory.createResourceDescription(
				NGramTagger.NGramList.class, NGramTagger.NGramList.PARAM_SOURCE_URL,
				getClass().getClassLoader().getResource("war-and-peace-ngramlist.txt").toString());
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(NGramTagger.class,
				NGramTagger.PARAM_TARGET_ANNOTATION, TestType.class, NGramTagger.RESOURCE_WORDLIST, erd));
		assertTrue(JCasUtil.exists(jcas, TestType.class));
		assertEquals(34677, JCasUtil.select(jcas, TestType.class).size());
	}
}

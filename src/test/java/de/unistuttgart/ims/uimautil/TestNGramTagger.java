package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.uimautil.api.TestType;

public class TestNGramTagger {
	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Der Hund bellt. Der Hund bellt. Die Hunde bellen. Ein schwarzer Hund bellt.");
		jcas.setDocumentLanguage("de");
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class),
				AnalysisEngineFactory.createEngineDescription(MateLemmatizer.class));
	}

	@Test
	public void testTagging() throws AnalysisEngineProcessException, ResourceInitializationException {
		assertTrue(JCasUtil.exists(jcas, Lemma.class));
		final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(
				NGramTagger.NGramList.class, NGramTagger.NGramList.PARAM_SOURCE_URL,
				getClass().getClassLoader().getResource("testngramlist.txt").toString());
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(NGramTagger.class,
				NGramTagger.PARAM_TARGET_ANNOTATION, TestType.class, NGramTagger.RESOURCE_WORDLIST, erd));
		assertEquals(4, JCasUtil.select(jcas, TestType.class).size());
		TestType tt = JCasUtil.selectByIndex(jcas, TestType.class, 0);
		assertEquals("Hund bellt", tt.getCoveredText());

		tt = JCasUtil.selectByIndex(jcas, TestType.class, 2);
		assertEquals("Hunde bellen", tt.getCoveredText());
	}

}

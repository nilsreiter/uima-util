package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.ims.uimautil.api.TestType;

public class TestWordListTagger {
	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, " + "consectetur adipisici elit, sed eiusmod "
				+ "tempor incidunt ut labore et dolore magna " + "aliqua. Ut enim ad minim veniam, quis "
				+ "nostrud exercitation ullamco laboris nisi " + "ut aliquid ex ea commodi consequat. Quis aute "
				+ "iure reprehenderit in voluptate velit esse " + "cillum dolore eu fugiat nulla pariatur. Excepteur "
				+ "sint obcaecat cupiditat non proident, sunt in" + " culpa qui officia deserunt mollit anim id est "
				+ "laborum.");
		AnnotationFactory.createAnnotation(jcas, 0, 5, Annotation.class);
		AnnotationFactory.createAnnotation(jcas, 6, 11, Annotation.class);
		AnnotationFactory.createAnnotation(jcas, 12, 17, Annotation.class);
	}

	@Test
	public void testTagging() throws AnalysisEngineProcessException, ResourceInitializationException {
		final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(
				WordListTagger.WordList.class, WordListTagger.WordList.PARAM_SOURCE_URL,
				getClass().getClassLoader().getResource("testwordlist.txt").toString());
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(WordListTagger.class,
						WordListTagger.PARAM_TARGET_ANNOTATION, TestType.class, WordListTagger.PARAM_BASE_ANNOTATION,
						Annotation.class.getCanonicalName(), WordListTagger.RESOURCE_WORDLIST, erd));
		assertEquals(1, JCasUtil.select(jcas, TestType.class).size());
		final TestType tt = JCasUtil.selectByIndex(jcas, TestType.class, 0);
		assertEquals("dolor", tt.getCoveredText());
	}

	@Test
	public void testPlainTagging() throws AnalysisEngineProcessException, ResourceInitializationException {
		final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(
				WordListTagger.WordList.class, WordListTagger.WordList.PARAM_SOURCE_URL,
				getClass().getClassLoader().getResource("testwordlist.txt").toString());
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(WordListTagger.class,
				WordListTagger.PARAM_TARGET_ANNOTATION, TestType.class, WordListTagger.RESOURCE_WORDLIST, erd));

		assertTrue(JCasUtil.exists(jcas, TestType.class));
		assertEquals(5, JCasUtil.select(jcas, TestType.class).size());
		TestType tt;

		int index;
		index = 0;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("dolor", tt.getCoveredText());

		index = 1;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("Ut", tt.getCoveredText());

		index = 2;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("minim", tt.getCoveredText());

		index = 3;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("eu", tt.getCoveredText());

	}

	@Test
	public void testPlainTaggingCI() throws AnalysisEngineProcessException, ResourceInitializationException {
		final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(
				WordListTagger.WordList.class, WordListTagger.WordList.PARAM_SOURCE_URL,
				getClass().getClassLoader().getResource("testwordlist.txt").toString());
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(WordListTagger.class,
						WordListTagger.PARAM_TARGET_ANNOTATION, TestType.class, WordListTagger.RESOURCE_WORDLIST, erd,
						WordListTagger.PARAM_CI, true));

		assertTrue(JCasUtil.exists(jcas, TestType.class));
		assertEquals(7, JCasUtil.select(jcas, TestType.class).size());
		TestType tt;

		int index;
		index = 0;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("dolor", tt.getCoveredText());

		index = 1;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("ut", tt.getCoveredText());

		index = 2;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("Ut", tt.getCoveredText());

		index = 3;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("minim", tt.getCoveredText());

		index = 4;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("ut", tt.getCoveredText());

		index = 5;
		tt = JCasUtil.selectByIndex(jcas, TestType.class, index);
		assertEquals("eu", tt.getCoveredText());

	}

	@Test
	public void testTaggingWithFeature() throws AnalysisEngineProcessException, ResourceInitializationException {
		final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(
				WordListTagger.WordList.class, WordListTagger.WordList.PARAM_SOURCE_URL,
				getClass().getResource("/testwordlist.txt").toString(), WordListTagger.WordList.PARAM_LIST_NAME, "bla");
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(WordListTagger.class,
						WordListTagger.PARAM_TARGET_ANNOTATION, TestType.class, WordListTagger.PARAM_BASE_ANNOTATION,
						Annotation.class.getCanonicalName(), WordListTagger.RESOURCE_WORDLIST, erd,
						WordListTagger.PARAM_TARGET_FEATURE, "MyFeature"));
		assertEquals(1, JCasUtil.select(jcas, TestType.class).size());
		final TestType tt = JCasUtil.selectByIndex(jcas, TestType.class, 0);
		assertEquals("dolor", tt.getCoveredText());
		assertNotNull(tt.getMyFeature());
		assertEquals("bla", tt.getMyFeature());
	}
}

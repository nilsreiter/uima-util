package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.uimautil.api.TestType;

public class TestWindowAnnotator {
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
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class));
	}

	@Test
	public void testWindowAnnotator() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(WindowAnnotator.class,
						WindowAnnotator.PARAM_BASE_ANNOTATION, Token.class, WindowAnnotator.PARAM_TARGET_ANNOTATION,
						TestType.class, WindowAnnotator.PARAM_WINDOW_SIZE, 10));

		assertTrue(JCasUtil.exists(jcas, TestType.class));
		assertEquals(8, JCasUtil.select(jcas, TestType.class).size());
		for (int i = 0; i < 7; i++) {
			final TestType tt = JCasUtil.selectByIndex(jcas, TestType.class, i);
			assertEquals(10, JCasUtil.selectCovered(Token.class, tt).size());
		}
	}
}

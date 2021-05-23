package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.tokit.BreakIteratorSegmenter;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unistuttgart.ims.uimautil.api.TestSegment;
import de.unistuttgart.ims.uimautil.api.TestType;

public class TestContextWindowAnnotator {

	JCas jcas;

	@Before
	public void setUp() throws ResourceInitializationException {
		JCasIterable iterable = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(TextReader.class, TextReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/war-and-peace-excerpt.txt", TextReader.PARAM_LANGUAGE, "en"),
				AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class));
		jcas = iterable.iterator().next();
	}

	@Test
	public void testContextWindowAnnotator() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(ContextWindowAnnotator.class,
						ContextWindowAnnotator.PARAM_BASE_ANNOTATION, TestType.class,
						ContextWindowAnnotator.PARAM_CONTEXT_CLASS, Sentence.class,
						ContextWindowAnnotator.PARAM_TARGET_ANNOTATION, TestSegment.class));

		assertFalse(JCasUtil.exists(jcas, TestSegment.class));

		AnnotationFactory.createAnnotation(jcas, 0, 1, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 1000, 1005, TestType.class);
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(ContextWindowAnnotator.class,
						ContextWindowAnnotator.PARAM_BASE_ANNOTATION, TestType.class,
						ContextWindowAnnotator.PARAM_CONTEXT_CLASS, Sentence.class,
						ContextWindowAnnotator.PARAM_TARGET_ANNOTATION, TestSegment.class));

		assertTrue(JCasUtil.exists(jcas, TestSegment.class));
		assertEquals(2, JCasUtil.select(jcas, TestSegment.class).size());

	}

}

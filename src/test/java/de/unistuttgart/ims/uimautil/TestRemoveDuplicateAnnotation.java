package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.ims.uimautil.api.TestType;

public class TestRemoveDuplicateAnnotation {

	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("This is a test.");
	}

	@Test
	public void testBasics() throws AnalysisEngineProcessException, ResourceInitializationException {
		AnnotationFactory.createAnnotation(jcas, 0, 4, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 0, 4, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 5, 7, TestType.class);

		assertEquals(3, JCasUtil.select(jcas, TestType.class).size());

		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(RemoveDuplicateAnnotations.class,
				RemoveDuplicateAnnotations.PARAM_TYPE, TestType.class));

		assertEquals(2, JCasUtil.select(jcas, TestType.class).size());
		assertEquals(0, JCasUtil.selectByIndex(jcas, TestType.class, 0).getBegin());
		assertEquals(4, JCasUtil.selectByIndex(jcas, TestType.class, 0).getEnd());

		assertEquals(5, JCasUtil.selectByIndex(jcas, TestType.class, 1).getBegin());
		assertEquals(7, JCasUtil.selectByIndex(jcas, TestType.class, 1).getEnd());

	}

	@Test
	public void testOverlappingAnnotations() throws AnalysisEngineProcessException, ResourceInitializationException {
		AnnotationFactory.createAnnotation(jcas, 0, 4, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 0, 5, TestType.class);

		AnnotationFactory.createAnnotation(jcas, 5, 7, TestType.class);

		assertEquals(3, JCasUtil.select(jcas, TestType.class).size());

		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(RemoveDuplicateAnnotations.class,
				RemoveDuplicateAnnotations.PARAM_TYPE, TestType.class));

		assertEquals(3, JCasUtil.select(jcas, TestType.class).size());

	}

	@Test
	public void testNoAnnotations() throws AnalysisEngineProcessException, ResourceInitializationException {
		assertEquals(0, JCasUtil.select(jcas, TestType.class).size());
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(RemoveDuplicateAnnotations.class,
				RemoveDuplicateAnnotations.PARAM_TYPE, TestType.class));
		assertEquals(0, JCasUtil.select(jcas, TestType.class).size());
	}
}

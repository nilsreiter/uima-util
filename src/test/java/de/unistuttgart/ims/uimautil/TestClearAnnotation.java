package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.ims.uimautil.api.TestType;

public class TestClearAnnotation {
	JCas jcas;
	Annotation anno;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.");
	}

	@Test
	public void testClearAnnotationAnnotation()
			throws AnalysisEngineProcessException,
			ResourceInitializationException {
		AnnotationFactory.createAnnotation(jcas, 0, 5, Annotation.class);
		assertTrue(JCasUtil.exists(jcas, Annotation.class));
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory
				.createEngineDescription(ClearAnnotation.class,
						ClearAnnotation.PARAM_TYPE,
						Annotation.class.getCanonicalName()));
		assertFalse(JCasUtil.exists(jcas, Annotation.class));
	}

	public void testClearTestAnnotation()
			throws AnalysisEngineProcessException,
			ResourceInitializationException {
		AnnotationFactory.createAnnotation(jcas, 0, 5, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 1, 6, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 2, 3, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 10, 13, TestType.class);

		assertTrue(JCasUtil.exists(jcas, Annotation.class));
		assertTrue(JCasUtil.exists(jcas, TestType.class));
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory
				.createEngineDescription(ClearAnnotation.class,
						ClearAnnotation.PARAM_TYPE,
						Annotation.class.getCanonicalName()));
		assertFalse(JCasUtil.exists(jcas, Annotation.class));
		assertFalse(JCasUtil.exists(jcas, TestType.class));

	}

}

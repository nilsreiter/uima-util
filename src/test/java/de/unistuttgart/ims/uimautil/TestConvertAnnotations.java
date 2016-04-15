package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.uimautil.api.TestSegment;
import de.unistuttgart.ims.uimautil.api.TestType;

public class TestConvertAnnotations {
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
		DocumentMetaData.create(jcas).setDocumentId("Lorem");
	}

	@Test
	public void testConvertS2B() throws AnalysisEngineProcessException, ResourceInitializationException {
		AnnotationFactory.createAnnotation(jcas, 0, 100, TestSegment.class);
		AnnotationFactory.createAnnotation(jcas, 100, 200, TestSegment.class);

		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(ConvertSegmentToBoundaryAnnotation.class,
						ConvertSegmentToBoundaryAnnotation.PARAM_BOUNDARY_ANNOTATION_TYPE, TestType.class,
						ConvertSegmentToBoundaryAnnotation.PARAM_SEGMENT_ANNOTATION_TYPE, TestSegment.class));

		assertTrue(JCasUtil.exists(jcas, TestType.class));
		assertEquals(3, JCasUtil.select(jcas, TestType.class).size());

		TestType tt = JCasUtil.selectByIndex(jcas, TestType.class, 0);
		assertEquals(0, tt.getBegin());

		tt = JCasUtil.selectByIndex(jcas, TestType.class, 1);
		assertEquals(100, tt.getBegin());

		tt = JCasUtil.selectByIndex(jcas, TestType.class, 2);
		assertEquals(200, tt.getBegin());

	}

	@Test
	public void testConvertB2S() throws AnalysisEngineProcessException, ResourceInitializationException {
		AnnotationFactory.createAnnotation(jcas, 0, 0, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 100, 100, TestType.class);
		AnnotationFactory.createAnnotation(jcas, 200, 200, TestType.class);

		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(ConvertBoundaryToSegmentAnnotation.class,
						ConvertBoundaryToSegmentAnnotation.PARAM_BOUNDARY_ANNOTATION_TYPE, TestType.class,
						ConvertBoundaryToSegmentAnnotation.PARAM_SEGMENT_ANNOTATION_TYPE, TestSegment.class));

		assertTrue(JCasUtil.exists(jcas, TestSegment.class));
		assertEquals(2, JCasUtil.select(jcas, TestSegment.class).size());

		TestSegment ts;

		ts = JCasUtil.selectByIndex(jcas, TestSegment.class, 0);
		assertEquals(0, ts.getBegin());
		assertEquals(100, ts.getEnd());

		ts = JCasUtil.selectByIndex(jcas, TestSegment.class, 1);
		assertEquals(100, ts.getBegin());
		assertEquals(200, ts.getEnd());
	}
}

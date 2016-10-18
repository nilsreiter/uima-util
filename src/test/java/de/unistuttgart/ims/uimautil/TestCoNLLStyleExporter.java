package de.unistuttgart.ims.uimautil;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.uimautil.api.TestSegment;

public class TestCoNLLStyleExporter {
	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText(
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.");
		Token[] tokens = new Token[] { AnnotationFactory.createAnnotation(jcas, 0, 5, Token.class),
				AnnotationFactory.createAnnotation(jcas, 6, 11, Token.class) };
		POS[] poss = new POS[] { AnnotationFactory.createAnnotation(jcas, 0, 5, POS.class),
				AnnotationFactory.createAnnotation(jcas, 6, 11, POS.class) };
		poss[0].setPosValue("A");
		poss[1].setPosValue("B");
		tokens[0].setPos(poss[0]);
		tokens[1].setPos(poss[1]);
		AnnotationFactory.createAnnotation(jcas, 0, 20, TestSegment.class);

	}

	@Test(expected = ResourceInitializationException.class)
	public void testExporter1() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(CoNLLStyleExporter.class));
	}

	@Test
	public void testExporter2() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(CoNLLStyleExporter.class,
						CoNLLStyleExporter.PARAM_ANNOTATION_CLASS, Token.class, CoNLLStyleExporter.PARAM_OUTPUT_FILE,
						"target/conll2.csv", CoNLLStyleExporter.PARAM_CONFIGURATION_FILE,
						"file:src/test/resources/CoNLLExportConfig-1.ini"));
	}

	@Test
	public void testExporter3() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(CoNLLStyleExporter.class,
						CoNLLStyleExporter.PARAM_ANNOTATION_CLASS, TestSegment.class,
						CoNLLStyleExporter.PARAM_OUTPUT_FILE, "target/conll3.csv",
						CoNLLStyleExporter.PARAM_CONFIGURATION_FILE, "file:src/test/resources/CoNLLExportConfig-1.ini"));
	}

}

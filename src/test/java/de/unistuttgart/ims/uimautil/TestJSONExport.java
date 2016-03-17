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

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.unistuttgart.ims.uimautil.api.TestType;

public class TestJSONExport {
	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("bla bla blubb");
		DocumentMetaData.create(jcas).setDocumentId("test");
		AnnotationFactory.createAnnotation(jcas, 0, 5, TestType.class);

	}

	@Test
	public void testExport() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(JSONExport.class,
				JSONExport.PARAM_TARGET_LOCATION, "target/json/"));
	}
}

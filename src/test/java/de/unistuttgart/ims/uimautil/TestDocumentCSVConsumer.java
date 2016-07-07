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

public class TestDocumentCSVConsumer {
	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet");
		DocumentMetaData.create(jcas).setDocumentId("test");
		TestType tt = AnnotationFactory.createAnnotation(jcas, 0, 5, TestType.class);
		tt.setMyFeature("Hello");
		tt.setMyIntFeature(5);
	}

	@Test
	public void testDocumentCSVConsumer() throws AnalysisEngineProcessException, ResourceInitializationException {
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(DocumentCSVConsumer.class,
						DocumentCSVConsumer.PARAM_ANNOTATION_TYPE, TestType.class,
						DocumentCSVConsumer.PARAM_LAST_FEATURE, "MyFeature", DocumentCSVConsumer.PARAM_TARGET_LOCATION,
						"target", DocumentCSVConsumer.PARAM_USE_DOCUMENT_ID, true));
	}
}

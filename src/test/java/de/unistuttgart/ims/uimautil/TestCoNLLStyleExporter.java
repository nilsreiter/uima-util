package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(CoNLLCasConsumer.class));
	}

	@Test
	public void testExporter2()
			throws AnalysisEngineProcessException, ResourceInitializationException, FileNotFoundException, IOException {
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(CoNLLCasConsumer.class,
						CoNLLCasConsumer.PARAM_ANNOTATION_CLASS, Token.class, CoNLLCasConsumer.PARAM_OUTPUT_FILE,
						"target/conll2.csv", CoNLLCasConsumer.PARAM_CONFIGURATION_FILE,
						"file:src/test/resources/CoNLLExportConfig.ini"));

		CSVParser csvp = new CSVParser(new FileReader("target/conll2.csv"), CSVFormat.DEFAULT.withHeader());
		Iterator<CSVRecord> iter = csvp.iterator();
		assertTrue(iter.hasNext());

		CSVRecord rec = iter.next();
		assertEquals("0", rec.get(0));
		assertEquals("5", rec.get(1));
		assertEquals("Lorem", rec.get(2));

		rec = iter.next();
		assertEquals("6", rec.get(0));
		assertEquals("11", rec.get(1));
		assertEquals("ipsum", rec.get(2));

		assertFalse(iter.hasNext());
		csvp.close();
	}

	@Test
	public void testExporter3()
			throws AnalysisEngineProcessException, ResourceInitializationException, FileNotFoundException, IOException {
		SimplePipeline.runPipeline(jcas,
				AnalysisEngineFactory.createEngineDescription(CoNLLCasConsumer.class,
						CoNLLCasConsumer.PARAM_ANNOTATION_CLASS, TestSegment.class,
						CoNLLCasConsumer.PARAM_OUTPUT_FILE, "target/conll3.csv",
						CoNLLCasConsumer.PARAM_CONFIGURATION_FILE, "file:src/test/resources/CoNLLExportConfig2.ini"));

		CSVParser csvp = new CSVParser(new FileReader("target/conll3.csv"), CSVFormat.DEFAULT.withHeader());
		Iterator<CSVRecord> iter = csvp.iterator();
		assertTrue(iter.hasNext());

		CSVRecord rec = iter.next();
		assertEquals("", rec.get(0));
		assertEquals("2", rec.get(1));
		assertEquals("0", rec.get(2));
		assertEquals("20", rec.get(3));
		assertEquals("0", rec.get(4));
		assertEquals("5", rec.get(5));
		assertEquals("Lorem", rec.get(6));

		rec = iter.next();
		assertEquals("", rec.get(0));
		assertEquals("2", rec.get(1));
		assertEquals("0", rec.get(2));
		assertEquals("20", rec.get(3));
		assertEquals("6", rec.get(4));
		assertEquals("11", rec.get(5));
		assertEquals("ipsum", rec.get(6));

		assertFalse(iter.hasNext());
		csvp.close();

	}

}

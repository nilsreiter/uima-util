package de.unistuttgart.ims.uimautil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.OperationalProperties;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

@OperationalProperties(multipleDeploymentAllowed = false)
public class CoNLLStyleExporter extends JCasConsumer_ImplBase {

	public static final String PARAM_OUTPUT_FILE = "Output File";

	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true)
	File outputFile;

	CSVPrinter csvPrinter;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			csvPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.TDF);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		try {
			csvPrinter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AnalysisEngineProcessException(e);
		}
		IOUtils.closeQuietly(csvPrinter);
	}

}

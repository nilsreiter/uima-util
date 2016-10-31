package de.unistuttgart.ims.uimautil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.OperationalProperties;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

@OperationalProperties(multipleDeploymentAllowed = false)
public class CoNLLCasConsumer extends JCasConsumer_ImplBase {

	public static final String PARAM_OUTPUT_FILE = "Output File";
	public static final String PARAM_CONFIGURATION_FILE = "Configuration URL";

	public static final String PARAM_ANNOTATION_CLASS = "Annotation Class";
	public static final String PARAM_FEATURE_PATHS = "Feature Paths";
	public static final String PARAM_COLUMN_LABELS = "Feature Path Labels";
	public static final String PARAM_COVERED_ANNOTATION_CLASS = "Covered Annotation Class";

	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true)
	File outputFile;

	@ConfigurationParameter(name = PARAM_ANNOTATION_CLASS, defaultValue = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token")
	String annotationClassName;

	@ConfigurationParameter(name = PARAM_CONFIGURATION_FILE)
	String configurationFile;

	@ConfigurationParameter(name = PARAM_COVERED_ANNOTATION_CLASS, mandatory = false)
	String coveredAnnotationClassName = null;

	CSVPrinter csvPrinter;

	CombinedConfiguration config;

	Type type;
	CoNLLExport exporter;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		INIConfiguration defaultConfig = new INIConfiguration();
		INIConfiguration serverConfig = new INIConfiguration();

		InputStream is = null;
		try {
			// reading of default properties from inside the jar
			is = getClass().getResourceAsStream("/project.properties");
			if (is != null) {
				defaultConfig.read(new InputStreamReader(is, "UTF-8"));
				// defaults.load();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		try {
			// reading additional properties in seperate file, as specified
			// in the context
			URL url = new URL(configurationFile);
			is = url.openStream();
			serverConfig.read(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		config = new CombinedConfiguration(new OverrideCombiner());
		config.addConfiguration(serverConfig);
		config.addConfiguration(defaultConfig);
		Class<? extends Annotation> annotationClass = null;
		Class<? extends Annotation> coveredAnnotationClass = null;

		Class<?> cl = null;
		try {
			cl = Class.forName(annotationClassName);
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
			throw new ResourceInitializationException(e2);
		}
		if (cl != null)
			annotationClass = (Class<? extends Annotation>) cl;

		cl = null;
		if (coveredAnnotationClassName != null)
			try {
				cl = Class.forName(coveredAnnotationClassName);
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
				throw new ResourceInitializationException(e2);
			}
		if (cl != null)
			coveredAnnotationClass = (Class<? extends Annotation>) cl;

		JCas jcas;

		try {
			jcas = JCasFactory.createJCas();
		} catch (UIMAException e) {
			throw new ResourceInitializationException(e);
		}

		exporter = new CoNLLExport();
		try {
			exporter.init(config, jcas, annotationClass, coveredAnnotationClass);
		} catch (UIMAException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);

		}

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		exporter.convert(jcas);
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		try {
			csvPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT);
			for (List<Object> row : exporter.getResult()) {
				csvPrinter.printRecord(row);
			}

			csvPrinter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AnalysisEngineProcessException(e);
		}
		IOUtils.closeQuietly(csvPrinter);
	}

}

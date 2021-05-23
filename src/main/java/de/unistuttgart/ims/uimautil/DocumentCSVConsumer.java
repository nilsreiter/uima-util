package de.unistuttgart.ims.uimautil;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.FeatureDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;

/**
 * A UIMA component to generate a Weka-compatible arff-file from a set of
 * weka.core.Instances, based on an annotation type that has to be specified.
 * Features of type {@code uima.cas.String}, {@code uima.cas.Double} and
 * {@code uima.cas.Integer} are converted into string resp. numeric features.
 * This component generates an arff file for each document.
 * 
 * @author reiterns
 *
 */
public class DocumentCSVConsumer extends JCasFileWriter_ImplBase {

	public static final String PARAM_ANNOTATION_TYPE = "Annotation Type";
	public static final String PARAM_DATASET_NAME = "Dataset Name";
	public static final String PARAM_FIRST_FEATURE = "First Feature";
	public static final String PARAM_LAST_FEATURE = "Last Feature";

	/**
	 * The annotation type to use for instances.
	 */
	@ConfigurationParameter(name = PARAM_ANNOTATION_TYPE, mandatory = true)
	String annotationType = null;

	/**
	 * A name for the data set.
	 */
	@ConfigurationParameter(name = PARAM_DATASET_NAME, mandatory = false, defaultValue = "Data Set")
	String datasetName = "Data Set";

	/**
	 * Which feature is the class feature? It will be the last feature in the
	 * generated file.
	 */
	@ConfigurationParameter(name = PARAM_LAST_FEATURE, mandatory = false)
	String lastFeatureName;

	@ConfigurationParameter(name = PARAM_FIRST_FEATURE, mandatory = false)
	String firstFeatureName;

	TypeDescription typeDescription;

	Map<String, Integer> attributeMap;

	List<String> attributes;

	int numAttributes = 0;

	Class<? extends Annotation> cl;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		attributeMap = new HashMap<String, Integer>();
		attributes = new LinkedList<String>();
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		typeDescription = tsd.getType(annotationType);
		String lastAttribute = null;
		String firstAttribute = null;

		if (typeDescription == null)
			throw new ResourceInitializationException(ResourceInitializationException.COULD_NOT_INSTANTIATE,
					new String[] { annotationType });
		FeatureDescription[] fds = typeDescription.getFeatures();
		int f = 0;

		for (int i = 0; i < fds.length; i++) {

			String attr = null;
			FeatureDescription fd = fds[i];

			if (fd.getName().equals(lastFeatureName)) {
				lastAttribute = makeAttribute(fd);
			} else if (fd.getName().equals(firstFeatureName)) {
				firstAttribute = makeAttribute(fd);
			} else {
				attr = makeAttribute(fd);
			}
			if (attr != null) {
				attributes.add(attr);
				attributeMap.put(fd.getName(), f++);
			}
		}

		if (lastAttribute != null) {
			attributes.add(lastAttribute);
			attributeMap.put(lastFeatureName, f);
		}
		if (firstAttribute != null)
			attributes.add(0, firstAttribute);

		cl = TypeParameterUtil.getClass(annotationType);
	}

	protected String makeAttribute(FeatureDescription fd) {
		String rangeTypeName = fd.getRangeTypeName();

		Attribute attr = null;
		if (rangeTypeName.equals("uima.cas.String")) {
			attr = new Attribute(fd.getName());
			return fd.getName();
		} else if (rangeTypeName.equals("uima.cas.Integer")) {
			attr = new Attribute(fd.getName());
			return fd.getName();
		} else if (rangeTypeName.equals("uima.cas.Double")) {
			attr = new Attribute(fd.getName());
			return fd.getName();
		} else if (rangeTypeName.equals("uima.cas.Boolean")) {
			attr = new Attribute(fd.getName());
			return fd.getName();
		}
		// return attr;
		return null;
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		NamedOutputStream aw = null;
		PrintStream ps = null;
		CSVPrinter p = null;
		try {
			aw = getOutputStream(jcas, ".csv");
			ps = new PrintStream(aw);
			p = new CSVPrinter(ps, CSVFormat.DEFAULT);

			Type type = jcas.getTypeSystem().getType(typeDescription.getName());
			for (Annotation anno : JCasUtil.select(jcas, cl)) {

				for (String attr : attributes) {
					Feature feature = anno.getType().getFeatureByBaseName(attr);
					if (feature.getDomain().equals(type)) {
						if (feature.getRange().getName().equals("uima.cas.String")) {
							if (anno == null || anno.getFeatureValueAsString(feature) == null) {
								p.print(null);
							} else
								p.print(anno.getFeatureValueAsString(feature));
						} else if (feature.getRange().getName().equals("uima.cas.Integer")) {
							p.print(anno.getIntValue(feature));
						} else if (feature.getRange().getName().equals("uima.cas.Double")) {
							p.print(anno.getDoubleValue(feature));
						} else if (feature.getRange().getName().equals("uima.cas.Boolean")) {
							p.print(anno.getBooleanValue(feature));
						}
					}
				}
				p.println();
			}

			p.flush();
			ps.flush();
			aw.flush();
			p.close();
			ps.close();
			aw.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(ps);
			IOUtils.closeQuietly(aw);
			IOUtils.closeQuietly(p);
		}
	}

	public static class Attribute {
		String name;

		public Attribute(String s) {
			name = s;
		}

		public Attribute(String s, Object o) {
			name = s;
		}
	}
}

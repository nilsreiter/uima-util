package de.unistuttgart.ims.uimautil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeaturePath;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.OperationalProperties;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.uimautil.export.CoveredExportEntry;
import de.unistuttgart.ims.uimautil.export.Column;
import de.unistuttgart.ims.uimautil.export.FeaturePathColumn;
import de.unistuttgart.ims.uimautil.export.PrimitiveColumn;

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

	List<Column> eelist;

	Class<? extends Annotation> annotationClass;
	Class<? extends Annotation> coveredAnnotationClass = null;
	CombinedConfiguration config;

	Type type;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			csvPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}

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
		TypeSystemDescription tsd;
		try {
			jcas = JCasFactory.createJCas();
			tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		} catch (UIMAException e) {
			throw new ResourceInitializationException(e);
		}

		TypeDescription td = tsd.getType(annotationClass.getName());
		type = jcas.getTypeSystem().getType(td.getName());
		Type coveredType = null;
		if (coveredAnnotationClass != null) {
			coveredType = jcas.getTypeSystem().getType(coveredAnnotationClass.getName());
		}

		eelist = getExportEntries(jcas, type, coveredType);

		String confKey = type.getName().replaceAll("\\.", "..") + ".fixed";
		String[] confValues = config.getString(confKey, "").split(",");
		String[] confValueLabels = config.getString(confKey + "_labels", "").split(",");

		for (int i = 0; i < confValues.length; i++) {
			String confEntry = confValues[i].trim();
			String confEntryLabel = confValueLabels[i].trim();
			if (confEntry.equalsIgnoreCase("DocumentId")) {
				eelist.add(0, new Column(new String[] { confEntryLabel }) {

					@Override
					public Object getValue(Annotation a) {
						try {
							return DocumentMetaData.get(a.getCAS()).getDocumentId();
						} catch (Exception e) {
							return "";
						}
					}

					@Override
					public boolean isMultiplying() {
						return false;
					}
				});
			} else if (confEntry.equalsIgnoreCase("Length")) {
				eelist.add(0, new Column(new String[] { confEntryLabel }) {

					@Override
					public Object getValue(Annotation a) {
						try {
							JCas jcas = a.getCAS().getJCas();
							return JCasUtil.select(jcas, Token.class).size();
						} catch (CASException e) {
							e.printStackTrace();
							return 0;
						}

					}

					@Override
					public boolean isMultiplying() {
						return false;
					}
				});
			}
		}

		// assemble the header
		List<Object> header = new LinkedList<Object>();
		for (Column ee : eelist) {
			for (String s : ee.getLabel()) {
				header.add(s);
			}
		}

		// print the header
		try {
			csvPrinter.printRecord(header);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Collection<? extends Annotation> annotationList = JCasUtil.select(jcas, annotationClass);

		// print entries
		for (Annotation a : annotationList) {

			for (List<Object> l : printFeatureValues(a, eelist.iterator())) {
				try {
					csvPrinter.printRecord(l);
				} catch (IOException e) {
					e.printStackTrace();
					throw new AnalysisEngineProcessException(e);
				}
			}
		}
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

	private ArrayList<ArrayList<Object>> printFeatureValues(Annotation a, Iterator<Column> eelist) {
		ArrayList<ArrayList<Object>> r = new ArrayList<ArrayList<Object>>();
		r.add(new ArrayList<Object>());

		while (eelist.hasNext()) {
			Column ee = eelist.next();
			Object value = ee.getValue(a);
			if (ee.isMultiplying()) {
				ArrayList<ArrayList<ArrayList<Object>>> clones = new ArrayList<ArrayList<ArrayList<Object>>>();
				Object[] vals = (Object[]) value;
				for (int j = 1; j < vals.length; j++) {
					clones.add(deepClone(r));
				}

				for (List<Object> l : r) {
					if (vals.length == 0) {
						l.add("");
					} else
						for (Object o : (Object[]) vals[0]) {
							l.add(o);
						}
				}

				for (int j = 1; j < vals.length; j++) {
					for (ArrayList<Object> l : clones.get(j - 1)) {
						for (Object o : (Object[]) vals[j]) {
							l.add(o);
						}
					}
					r.addAll(clones.get(j - 1));
				}
			} else {
				for (List<Object> l : r) {
					l.add(value);
				}
			}
		}
		return r;

	}

	private String[] getExportPathsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".paths";
		String confEntry = config.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

	private String[] getPathLabelsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".labels";
		String confEntry = config.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

	@SuppressWarnings("unchecked")
	private ArrayList<ArrayList<Object>> deepClone(ArrayList<ArrayList<Object>> list) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		for (ArrayList<Object> l : list) {
			ret.add((ArrayList<Object>) l.clone());
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	protected List<Column> getExportEntries(JCas jcas, Type type, Type coveredType) {
		List<Column> eelist = new LinkedList<Column>();

		for (Feature fd : type.getFeatures()) {
			if (fd.getRange().isPrimitive()) {
				PrimitiveColumn pee = new PrimitiveColumn(
						jcas.getTypeSystem().getFeatureByFullName(fd.getName()));
				eelist.add(pee);
			}
		}
		String[] paths = this.getExportPathsForType(type);
		String[] labels = this.getPathLabelsForType(type);
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			FeaturePath fp = jcas.createFeaturePath();
			try {
				fp.initialize(path);
				fp.typeInit(type);
				if (labels.length > i) {
					eelist.add(new FeaturePathColumn(fp, labels[i]));
				} else {
					eelist.add(new FeaturePathColumn(fp));
				}
			} catch (CASException e) {
				e.printStackTrace();

			}
		}
		String[] covTypes = this.getCoveringsForType(type);
		for (int j = 0; j < covTypes.length; j++) {
			paths = getExportPathsForType(jcas.getTypeSystem().getType(covTypes[j]));
			labels = getPathLabelsForType(jcas.getTypeSystem().getType(covTypes[j]));
			FeaturePath[] path = new FeaturePath[paths.length];
			for (int i = 0; i < path.length; i++) {
				path[i] = jcas.createFeaturePath();
				try {
					labels[i] = Class.forName(covTypes[j]).getSimpleName() + "/" + labels[i];
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				try {
					path[i].initialize(paths[i]);
					path[i].typeInit(jcas.getTypeSystem().getType(covTypes[j]));
				} catch (CASException e) {
					e.printStackTrace();
				}
			}
			try {
				Column ee = new CoveredExportEntry((Class<? extends Annotation>) Class.forName(covTypes[j]), path);
				ee.setLabel(labels);
				eelist.add(ee);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (coveredType != null)

		{
			labels = getPathLabelsForType(coveredType);
			paths = getExportPathsForType(coveredType);
			FeaturePath[] path = new FeaturePath[paths.length];
			for (int i = 0; i < path.length; i++) {
				path[i] = jcas.createFeaturePath();
				try {
					labels[i] = coveredType.getShortName() + "/" + labels[i];
					path[i].initialize(paths[i]);
					path[i].typeInit(coveredType);
				} catch (CASException e) {
					e.printStackTrace();
				}
			}
			try {
				Column ee = new CoveredExportEntry(
						(Class<? extends Annotation>) Class.forName(coveredType.getName()), path);
				ee.setLabel(labels);
				eelist.add(ee);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
		return eelist;

	}

	protected String[] getCoveringsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".covered";
		String confEntry = config.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

}

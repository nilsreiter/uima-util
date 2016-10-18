package de.unistuttgart.ims.uimautil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
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

import de.unistuttgart.ims.uimautil.export.ExportEntry;
import de.unistuttgart.ims.uimautil.export.FeaturePathExportEntry;

@OperationalProperties(multipleDeploymentAllowed = false)
public class CoNLLStyleExporter extends JCasConsumer_ImplBase {

	public static final String PARAM_OUTPUT_FILE = "Output File";
	public static final String PARAM_ANNOTATION_CLASS = "Annotation Class";
	public static final String PARAM_FEATURE_PATHS = "Feature Paths";
	public static final String PARAM_COLUMN_LABELS = "Feature Path Labels";

	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true)
	File outputFile;

	@ConfigurationParameter(name = PARAM_ANNOTATION_CLASS, defaultValue = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token")
	String annotationClassName;

	@ConfigurationParameter(name = PARAM_FEATURE_PATHS, mandatory = false)
	String[] paths = new String[] {};

	@ConfigurationParameter(name = PARAM_COLUMN_LABELS, mandatory = false)
	String[] labels = new String[] {};

	CSVPrinter csvPrinter;

	List<ExportEntry> eelist;

	Class<? extends Annotation> annotationClass;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			csvPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.TDF);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}

		try {
			annotationClass = (Class<? extends Annotation>) Class.forName(annotationClassName);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			throw new ResourceInitializationException(e1);
		}

		eelist = new LinkedList<ExportEntry>();

		JCas jcas;
		TypeSystemDescription tsd;
		try {
			jcas = JCasFactory.createJCas();
			tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		} catch (UIMAException e) {
			throw new ResourceInitializationException(e);
		}

		TypeDescription td = tsd.getType(annotationClass.getName());
		Type type = jcas.getTypeSystem().getType(td.getName());

		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			FeaturePath fp = jcas.createFeaturePath();
			try {
				fp.initialize(path);
				fp.typeInit(type);
				if (labels.length > i) {
					eelist.add(new FeaturePathExportEntry(fp, labels[i]));
				} else {
					eelist.add(new FeaturePathExportEntry(fp));
				}
			} catch (CASException e) {
				e.printStackTrace();
			}
		}

		// assemble the header
		List<Object> header = new LinkedList<Object>();
		for (ExportEntry ee : eelist) {
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
			for (List<Object> l : printFeatureValues(a, eelist)) {
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

	private ArrayList<ArrayList<Object>> printFeatureValues(Annotation a, List<ExportEntry> eelist) {
		ArrayList<ArrayList<Object>> r = new ArrayList<ArrayList<Object>>();
		r.add(new ArrayList<Object>());

		for (int i = 0; i < eelist.size(); i++) {
			ExportEntry ee = eelist.get(i);
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

	@SuppressWarnings("unchecked")
	private ArrayList<ArrayList<Object>> deepClone(ArrayList<ArrayList<Object>> list) {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		for (ArrayList<Object> l : list) {
			ret.add((ArrayList<Object>) l.clone());
		}
		return ret;
	}

}

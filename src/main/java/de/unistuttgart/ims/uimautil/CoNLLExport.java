package de.unistuttgart.ims.uimautil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeaturePath;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.uimautil.export.Column;
import de.unistuttgart.ims.uimautil.export.CoveredColumn;
import de.unistuttgart.ims.uimautil.export.FeaturePathColumn;
import de.unistuttgart.ims.uimautil.export.PrimitiveColumn;

public class CoNLLExport {
	Configuration configuration;

	List<Column> columnList;
	Type type;
	Class<? extends TOP> annotationClass;
	Class<? extends Annotation> coveredAnnotationClass;
	List<List<Object>> result = new LinkedList<List<Object>>();

	public List<List<Object>> getResult() {
		return result;
	}

	public List<? extends List<Object>> convert(JCas jcas) {
		Collection<? extends TOP> annotationList = JCasUtil.select(jcas, annotationClass);

		// print entries
		for (TOP a : annotationList) {

			result.addAll(printFeatureValues(a, columnList.iterator()));

		}
		return result;
	}

	public void init(Configuration config, JCas jcas, Class<? extends TOP> annotationClass,
			Class<? extends Annotation> coveredAnnotationClass) throws UIMAException {
		this.configuration = config;
		this.annotationClass = annotationClass;
		this.coveredAnnotationClass = coveredAnnotationClass;
		this.result = new LinkedList<List<Object>>();

		TypeSystemDescription tsd;
		jcas = JCasFactory.createJCas();
		tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();

		TypeDescription td = tsd.getType(annotationClass.getName());
		type = jcas.getTypeSystem().getType(td.getName());
		Type coveredType = null;
		if (coveredAnnotationClass != null) {
			coveredType = jcas.getTypeSystem().getType(coveredAnnotationClass.getName());
		}

		columnList = getColumns(jcas, type, coveredType);

		String confKey = type.getName().replaceAll("\\.", "..") + ".fixed";
		String[] confValues = configuration.getString(confKey, "").split(",");
		String[] confValueLabels = configuration.getString(confKey + "_labels", "").split(",");

		for (int i = 0; i < confValues.length; i++) {
			String confEntry = confValues[i].trim();
			String confEntryLabel = confValueLabels[i].trim();
			if (confEntry.equalsIgnoreCase("DocumentId")) {
				columnList.add(0, new Column(new String[] { confEntryLabel }) {

					@Override
					public Object getValue(TOP a) {
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
				columnList.add(0, new Column(new String[] { confEntryLabel }) {

					@Override
					public Object getValue(TOP a) {
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
		for (Column ee : columnList) {
			for (String s : ee.getLabel()) {
				header.add(s);
			}
		}

		result.add(header);
	}

	private ArrayList<ArrayList<Object>> printFeatureValues(TOP a, Iterator<Column> eelist) {
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

	private String[] getFeaturePathsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".paths";
		String confEntry = configuration.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

	private String[] getColumnHeadersForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".labels";
		String confEntry = configuration.getString(confKey, null);
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
	private List<Column> getColumns(JCas jcas, Type type, Type coveredType) {
		List<Column> eelist = new LinkedList<Column>();

		for (Feature fd : type.getFeatures()) {
			if (fd.getRange().isPrimitive()) {
				PrimitiveColumn pee = new PrimitiveColumn(jcas.getTypeSystem().getFeatureByFullName(fd.getName()));
				eelist.add(pee);
			}
		}
		String[] paths = this.getFeaturePathsForType(type);
		String[] labels = this.getColumnHeadersForType(type);
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
			paths = getFeaturePathsForType(jcas.getTypeSystem().getType(covTypes[j]));
			labels = getColumnHeadersForType(jcas.getTypeSystem().getType(covTypes[j]));
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
				Column ee = new CoveredColumn((Class<? extends Annotation>) Class.forName(covTypes[j]), path);
				ee.setLabel(labels);
				eelist.add(ee);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (coveredType != null)

		{
			labels = getColumnHeadersForType(coveredType);
			paths = getFeaturePathsForType(coveredType);
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
				Column ee = new CoveredColumn((Class<? extends Annotation>) Class.forName(coveredType.getName()), path);
				ee.setLabel(labels);
				eelist.add(ee);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
		return eelist;

	}

	private String[] getCoveringsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".covered";
		String confEntry = configuration.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

	public void clearResult() {
		result.clear();
	}
}

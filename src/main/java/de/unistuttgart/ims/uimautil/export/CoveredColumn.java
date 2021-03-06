package de.unistuttgart.ims.uimautil.export;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.cas.FeaturePath;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

@Deprecated
public class CoveredColumn extends Column {

	Class<? extends Annotation> coveredClass;
	List<FeaturePathColumn> featurePathExportEntries;
	int useCovered = 1;

	public CoveredColumn(Class<? extends Annotation> clazz, FeaturePath... fps) {
		super(new String[fps.length]);
		coveredClass = clazz;
		featurePathExportEntries = new LinkedList<FeaturePathColumn>();
		for (int i = 0; i < fps.length; i++) {
			super.label[i] = clazz.getSimpleName() + fps[i].getFeaturePath();
			featurePathExportEntries.add(new FeaturePathColumn(fps[i]));
		}
	}

	@Override
	public Object getValue(TOP top) {
		Annotation a = (Annotation) top;
		List<? extends Annotation> l = JCasUtil.selectCovered(coveredClass, a);
		Object[][] r = new Object[l.size()][featurePathExportEntries.size()];

		/*
		 * This is a workaround, only needed if no speaker is present in an
		 * utterance
		 */
		if (l.isEmpty()) {
			r = new Object[1][featurePathExportEntries.size()];
			for (int j = 0; j < featurePathExportEntries.size(); j++) {
				r[0][j] = "";
			}
		} else
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < featurePathExportEntries.size(); j++) {
					try {
						r[i][j] = featurePathExportEntries.get(j).getValue(l.get(i));
					} catch (IndexOutOfBoundsException e) {
						r[i][j] = "";
					}
				}
			}
		return r;
	}

	@Override
	public boolean isMultiplying() {
		return true;
	}

}

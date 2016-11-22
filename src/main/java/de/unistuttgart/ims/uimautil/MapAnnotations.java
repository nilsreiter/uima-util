/*******************************************************************************
 * Copyright 2016 by Nils Reiter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.unistuttgart.ims.uimautil;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public class MapAnnotations extends JCasAnnotator_ImplBase {

	final static public String PARAM_SOURCE_CLASS = "Source Class";
	final static public String PARAM_TARGET_CLASS = "Target Class";
	final static public String PARAM_DELETE_SOURCE = "Delete Source";
	final static public String PARAM_FEATURE_NAME = "Target Feature Name";
	final static public String PARAM_FEATURE_VALUE = "Target Feature Value";

	@ConfigurationParameter(name = PARAM_TARGET_CLASS)
	String targetClassName;

	@ConfigurationParameter(name = PARAM_SOURCE_CLASS)
	String sourceClassName;

	@ConfigurationParameter(name = PARAM_DELETE_SOURCE, mandatory = false, defaultValue = "false")
	boolean deleteSource = false;

	@ConfigurationParameter(name = PARAM_FEATURE_NAME, mandatory = false, defaultValue = "")
	String targetFeatureName;

	@ConfigurationParameter(name = PARAM_FEATURE_VALUE, mandatory = false, defaultValue = "")
	String targetFeatureValue;

	Class<? extends Annotation> sourceClass;

	Class<? extends Annotation> targetClass;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			sourceClass = (Class<? extends Annotation>) Class.forName(sourceClassName);
			targetClass = (Class<? extends Annotation>) Class.forName(targetClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Set<Annotation> toDelete = null;
		if (deleteSource) {
			toDelete = new HashSet<Annotation>();
		}

		for (Annotation anno : JCasUtil.select(jcas, sourceClass)) {
			Annotation newAnnotation = AnnotationFactory.createAnnotation(jcas, anno.getBegin(), anno.getEnd(),
					targetClass);
			if (deleteSource)
				toDelete.add(anno);

			if (!(targetFeatureName.isEmpty() || targetFeatureValue.isEmpty())) {
				Feature feature = newAnnotation.getType().getFeatureByBaseName(targetFeatureName);
				if (feature != null)
					newAnnotation.setFeatureValueFromString(feature, targetFeatureValue);
			}
		}

		if (deleteSource) {
			for (Annotation a : toDelete) {
				a.removeFromIndexes();
			}
		}
	}

}

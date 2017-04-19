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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This component removes all annotations of a given type.
 *
 * @author reiterns
 * @since 0.2
 */
public class ClearAnnotation extends JCasAnnotator_ImplBase {

	public static final String PARAM_TYPE = "Type to Remove";

	public static final String PARAM_INCLUDE_SUBTYPES = "Include subtypes";

	@ConfigurationParameter(name = PARAM_TYPE)
	String type = null;
	Class<? extends Annotation> clazz;

	@ConfigurationParameter(name = PARAM_INCLUDE_SUBTYPES, mandatory = false, defaultValue = "true")
	boolean includeSubtypes = true;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		clazz = TypeParameterUtil.getClass(type);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			Annotation a = clazz.getConstructor(JCas.class).newInstance(jcas);
			if (includeSubtypes)
				jcas.removeAllIncludingSubtypes(a.getTypeIndexID());
			else
				jcas.removeAllExcludingSubtypes(a.getTypeIndexID());
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}

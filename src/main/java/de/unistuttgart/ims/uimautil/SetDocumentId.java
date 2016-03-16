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

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * This components sets the feature DocumentId in {@link DocumentMetaData}.
 *
 * @author reiterns
 * @since 0.4.1
 *
 */
public class SetDocumentId extends JCasAnnotator_ImplBase {

	public static final String PARAM_DOCUMENT_ID = "Document Id";

	@ConfigurationParameter(name = PARAM_DOCUMENT_ID)
	String documentId;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		DocumentMetaData.get(jcas).setDocumentId(documentId);
	}

}

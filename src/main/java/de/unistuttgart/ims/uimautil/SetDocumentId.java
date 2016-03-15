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

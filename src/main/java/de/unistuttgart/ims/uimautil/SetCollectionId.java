package de.unistuttgart.ims.uimautil;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class SetCollectionId extends JCasAnnotator_ImplBase {

	public static final String PARAM_COLLECTION_ID = "Collection Id";

	@ConfigurationParameter(name = PARAM_COLLECTION_ID)
	String collectionId;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (!JCasUtil.exists(aJCas, DocumentMetaData.class))
			DocumentMetaData.create(aJCas);
		DocumentMetaData.get(aJCas).setCollectionId(collectionId);

	}

}

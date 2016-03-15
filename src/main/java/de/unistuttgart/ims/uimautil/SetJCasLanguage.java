package de.unistuttgart.ims.uimautil;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

/**
 * This component sets the language of a jcas.
 *
 * @author reiterns
 * @since 0.2
 *
 */
public class SetJCasLanguage extends JCasAnnotator_ImplBase {

	public static final String PARAM_LANGUAGE = "Language";

	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
	String language = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		jcas.setDocumentLanguage(language);
	}

}

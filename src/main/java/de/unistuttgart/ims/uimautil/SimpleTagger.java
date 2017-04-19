package de.unistuttgart.ims.uimautil;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public abstract class SimpleTagger extends JCasAnnotator_ImplBase {

	public static final String PARAM_BASE_ANNOTATION = "Base Annotation";
	public static final String PARAM_TARGET_ANNOTATION = "Target Annotation";
	public static final String PARAM_TARGET_FEATURE = "Target Feature";
	public static final String PARAM_CI = "Casing";
	public static final String PARAM_LEMMA = "Lemma";
	@ConfigurationParameter(name = PARAM_BASE_ANNOTATION, mandatory = false)
	protected String baseAnnotationClassName = null;
	@ConfigurationParameter(name = PARAM_TARGET_ANNOTATION, mandatory = true)
	protected String targetAnnotationClassName = null;
	@ConfigurationParameter(name = PARAM_TARGET_FEATURE, mandatory = false)
	protected String targetFeatureName = null;
	@ConfigurationParameter(name = PARAM_CI, mandatory = false, defaultValue = "false")
	protected boolean caseIndependent = false;
	@ConfigurationParameter(name = PARAM_LEMMA, mandatory = false, defaultValue = "false")
	protected boolean listContainsLemmas = false;
	Class<? extends Annotation> targetAnnotation = null;
	Class<? extends Annotation> baseAnnotation = null;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		targetAnnotation = TypeParameterUtil.getClass(targetAnnotationClassName);
		baseAnnotation = TypeParameterUtil.getClass(baseAnnotationClassName);

	}

}

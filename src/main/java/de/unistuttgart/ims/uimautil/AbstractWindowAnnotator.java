package de.unistuttgart.ims.uimautil;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public abstract class AbstractWindowAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_BASE_ANNOTATION = "Base Annotation";
	public static final String PARAM_TARGET_ANNOTATION = "Target Annotation";

	@ConfigurationParameter(name = PARAM_BASE_ANNOTATION, mandatory = false)
	protected String baseAnnotationClassName = null;

	@ConfigurationParameter(name = PARAM_TARGET_ANNOTATION, mandatory = true)
	protected String targetAnnotationClassName = null;

	protected Class<? extends Annotation> targetAnnotation = null;

	protected Class<? extends Annotation> baseAnnotation = null;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		targetAnnotation = TypeParameterUtil.getClass(targetAnnotationClassName);
		baseAnnotation = TypeParameterUtil.getClass(baseAnnotationClassName, null);

	}

}

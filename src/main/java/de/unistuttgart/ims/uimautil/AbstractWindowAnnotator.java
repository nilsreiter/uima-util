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

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		Class<?> tA;

		try {
			tA = Class.forName(targetAnnotationClassName);
			if (Annotation.class.isAssignableFrom(tA))
				targetAnnotation = (Class<? extends Annotation>) tA;
			else
				throw new ResourceInitializationException();
		} catch (final ClassNotFoundException e1) {
			throw new ResourceInitializationException(e1);
		}

		try {
			if (baseAnnotationClassName != null) {
				tA = Class.forName(baseAnnotationClassName);
				if (Annotation.class.isAssignableFrom(tA))
					baseAnnotation = (Class<? extends Annotation>) tA;
				else
					throw new ResourceInitializationException();
			}
		} catch (final ClassNotFoundException e1) {
			throw new ResourceInitializationException(e1);
		}

	}

}

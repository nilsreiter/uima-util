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

package de.unistuttgart.ims.uimautil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public class CopyAnnotations extends JCasAnnotator_ImplBase {

	public static final String PARAM_SOURCE_VIEW = "Source View";
	public static final String PARAM_TARGET_VIEW = "Target View";
	public static final String PARAM_ANNOTATION_TYPE = "Annotation Type";

	@ConfigurationParameter(name = PARAM_SOURCE_VIEW)
	String sourceViewName;

	@ConfigurationParameter(name = PARAM_TARGET_VIEW)
	String targetViewName;

	@ConfigurationParameter(name = PARAM_ANNOTATION_TYPE)
	String annotationTypeName;
	Class<? extends Annotation> clazz;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		Class<?> cl;
		try {
			cl = Class.forName(annotationTypeName);
		} catch (final ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}

		clazz = (Class<? extends Annotation>) cl;

	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		JCas srcView, tgtView;
		try {
			srcView = aJCas.getView(sourceViewName);
			tgtView = aJCas.getView(targetViewName);
			for (Annotation a : JCasUtil.select(srcView, clazz)) {
				AnnotationFactory.createAnnotation(tgtView, a.getBegin(), a.getEnd(), clazz);
			}
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}

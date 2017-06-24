package de.unistuttgart.ims.uimautil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public class ConvertSegmentToBoundaryAnnotation extends JCasAnnotator_ImplBase {
	public static final String PARAM_SEGMENT_ANNOTATION_TYPE = "Segment Annotation Type";
	public static final String PARAM_BOUNDARY_ANNOTATION_TYPE = "Boundary Annotation Type";

	@ConfigurationParameter(name = PARAM_SEGMENT_ANNOTATION_TYPE)
	String segmentAnnotationTypeName;

	@ConfigurationParameter(name = PARAM_BOUNDARY_ANNOTATION_TYPE)
	String boundaryAnnotationTypeName;

	Class<? extends Annotation> segmentAnnotationType;

	Class<? extends Annotation> boundaryAnnotationType;

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		segmentAnnotationType = TypeParameterUtil.getClass(segmentAnnotationTypeName);
		boundaryAnnotationType = TypeParameterUtil.getClass(boundaryAnnotationTypeName);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		int e = 0;
		for (final Annotation anno : JCasUtil.select(jcas, segmentAnnotationType)) {
			final int b = anno.getBegin();
			AnnotationFactory.createAnnotation(jcas, b, b, boundaryAnnotationType);
			e = anno.getEnd();
		}
		AnnotationFactory.createAnnotation(jcas, e, e, boundaryAnnotationType);
	}

}

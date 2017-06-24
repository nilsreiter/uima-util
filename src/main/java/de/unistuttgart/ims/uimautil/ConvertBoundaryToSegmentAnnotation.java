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

public class ConvertBoundaryToSegmentAnnotation extends JCasAnnotator_ImplBase {

	public static final String PARAM_SEGMENT_ANNOTATION_TYPE = "Segment Annotation Type";
	public static final String PARAM_BOUNDARY_ANNOTATION_TYPE = "Boundary Annotation Type";
	public static final String PARAM_BEGIN_END = "Add segments for the entire document";

	@ConfigurationParameter(name = PARAM_SEGMENT_ANNOTATION_TYPE)
	String segmentAnnotationTypeName;

	@ConfigurationParameter(name = PARAM_BOUNDARY_ANNOTATION_TYPE)
	String boundaryAnnotationTypeName;

	@ConfigurationParameter(name = PARAM_BEGIN_END, mandatory = false)
	boolean beginAndSegment = false;

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
		int i = 0;
		int index = 0;
		for (final Annotation anno : JCasUtil.select(jcas, boundaryAnnotationType)) {
			if ((index > 0) || beginAndSegment)
				AnnotationFactory.createAnnotation(jcas, i, anno.getBegin(), segmentAnnotationType);
			i = anno.getBegin();
			index++;
		}
		if (beginAndSegment)
			AnnotationFactory.createAnnotation(jcas, i, jcas.getDocumentText().length(), segmentAnnotationType);
	}

}

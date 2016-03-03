package de.unistuttgart.ims.uimautil;

import static org.apache.uima.fit.factory.AnnotationFactory.createAnnotation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Before;
import org.junit.Test;

public class TestIMSUtil {
	JCas jcas;
	Annotation anno;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum     dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.");
	}

	@Test
	public void testTrim() {
		anno = createAnnotation(jcas, 0, 5, Annotation.class);
		anno = IMSUtil.trim(anno);
		assertFalse(anno.getCoveredText().matches(" "));
		assertEquals(0, anno.getBegin());
		assertEquals(5, anno.getEnd());
		assertEquals("Lorem", anno.getCoveredText());

		anno = createAnnotation(jcas, 0, 6, Annotation.class);
		anno = IMSUtil.trim(anno);
		assertEquals(0, anno.getBegin());
		assertEquals(5, anno.getEnd());
		assertEquals("Lorem", anno.getCoveredText());

		anno = createAnnotation(jcas, 5, 11, Annotation.class);
		anno = IMSUtil.trim(anno);
		assertEquals(6, anno.getBegin());
		assertEquals(11, anno.getEnd());
		assertEquals("ipsum", anno.getCoveredText());

		anno = IMSUtil.trim(createAnnotation(jcas, 12, 14, Annotation.class));
		assertEquals(12, anno.getBegin());
		assertEquals(14, anno.getEnd());
	}

}

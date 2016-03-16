/*******************************************************************************
 * Copyright 2016 by Nils Reiter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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

public class TestAnnotationUtil {
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
		anno = AnnotationUtil.trim(anno);
		assertFalse(anno.getCoveredText().matches(" "));
		assertEquals(0, anno.getBegin());
		assertEquals(5, anno.getEnd());
		assertEquals("Lorem", anno.getCoveredText());

		anno = createAnnotation(jcas, 0, 6, Annotation.class);
		anno = AnnotationUtil.trim(anno);
		assertEquals(0, anno.getBegin());
		assertEquals(5, anno.getEnd());
		assertEquals("Lorem", anno.getCoveredText());

		anno = createAnnotation(jcas, 5, 11, Annotation.class);
		anno = AnnotationUtil.trim(anno);
		assertEquals(6, anno.getBegin());
		assertEquals(11, anno.getEnd());
		assertEquals("ipsum", anno.getCoveredText());

		anno = AnnotationUtil.trim(createAnnotation(jcas, 12, 14, Annotation.class));
		assertEquals(12, anno.getBegin());
		assertEquals(14, anno.getEnd());
	}

}

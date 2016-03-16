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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.uimautil.MapAnnotations;

public class TestMapAnnotations {
	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, "
				+ "consectetur adipisici elit, sed eiusmod "
				+ "tempor incidunt ut labore et dolore magna "
				+ "aliqua. Ut enim ad minim veniam, quis "
				+ "nostrud exercitation ullamco laboris nisi "
				+ "ut aliquid ex ea commodi consequat. Quis aute "
				+ "iure reprehenderit in voluptate velit esse "
				+ "cillum dolore eu fugiat nulla pariatur. Excepteur "
				+ "sint obcaecat cupiditat non proident, sunt in"
				+ " culpa qui officia deserunt mollit anim id est "
				+ "laborum.");

		AnnotationFactory.createAnnotation(jcas, 0, 5, Token.class);
		AnnotationFactory.createAnnotation(jcas, 10, 18, Token.class);
		AnnotationFactory.createAnnotation(jcas, 20, 21, Token.class);
		AnnotationFactory.createAnnotation(jcas, 30, 30, Token.class);
		AnnotationFactory.createAnnotation(jcas, 32, 42, Token.class);
		AnnotationFactory.createAnnotation(jcas, 41, 45, Token.class);
	}

	@Test
	public void testWithoutDelete() throws AnalysisEngineProcessException,
			ResourceInitializationException {
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory
				.createEngineDescription(MapAnnotations.class,
						MapAnnotations.PARAM_SOURCE_CLASS,
						Token.class.getCanonicalName(),
						MapAnnotations.PARAM_TARGET_CLASS,
						Sentence.class.getCanonicalName()));

		assertTrue(JCasUtil.exists(jcas, Sentence.class));
		assertTrue(JCasUtil.exists(jcas, Token.class));
		assertEquals(6, JCasUtil.select(jcas, Sentence.class).size());
		assertEquals(6, JCasUtil.select(jcas, Token.class).size());
		Sentence s;
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 0);
		assertEquals(0, s.getBegin());
		assertEquals(5, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 1);
		assertEquals(10, s.getBegin());
		assertEquals(18, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 2);
		assertEquals(20, s.getBegin());
		assertEquals(21, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 3);
		assertEquals(30, s.getBegin());
		assertEquals(30, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 4);
		assertEquals(32, s.getBegin());
		assertEquals(42, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 5);
		assertEquals(41, s.getBegin());
		assertEquals(45, s.getEnd());
	}

	@Test
	public void testWithDelete() throws AnalysisEngineProcessException,
			ResourceInitializationException {
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory
				.createEngineDescription(MapAnnotations.class,
						MapAnnotations.PARAM_SOURCE_CLASS,
						Token.class.getCanonicalName(),
						MapAnnotations.PARAM_TARGET_CLASS,
						Sentence.class.getCanonicalName(),
						MapAnnotations.PARAM_DELETE_SOURCE, true));

		assertTrue(JCasUtil.exists(jcas, Sentence.class));
		assertFalse(JCasUtil.exists(jcas, Token.class));
		assertEquals(6, JCasUtil.select(jcas, Sentence.class).size());
		assertEquals(0, JCasUtil.select(jcas, Token.class).size());
		Sentence s;
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 0);
		assertEquals(0, s.getBegin());
		assertEquals(5, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 1);
		assertEquals(10, s.getBegin());
		assertEquals(18, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 2);
		assertEquals(20, s.getBegin());
		assertEquals(21, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 3);
		assertEquals(30, s.getBegin());
		assertEquals(30, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 4);
		assertEquals(32, s.getBegin());
		assertEquals(42, s.getEnd());
		s = JCasUtil.selectByIndex(jcas, Sentence.class, 5);
		assertEquals(41, s.getBegin());
		assertEquals(45, s.getEnd());
	}
}

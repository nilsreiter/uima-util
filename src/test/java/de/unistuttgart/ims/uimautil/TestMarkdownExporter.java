package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

public class TestMarkdownExporter {
	JCas jcas;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText("Lorem ipsum dolor sit amet, " + "consectetur adipisici elit, sed eiusmod "
				+ "tempor incidunt ut labore et dolore magna " + "aliqua. Ut enim ad minim veniam, quis "
				+ "nostrud exercitation ullamco laboris nisi " + "ut aliquid ex ea commodi consequat. Quis aute "
				+ "iure reprehenderit in voluptate velit esse " + "cillum dolore eu fugiat nulla pariatur. Excepteur "
				+ "sint obcaecat cupiditat non proident, sunt in" + " culpa qui officia deserunt mollit anim id est "
				+ "laborum.");

		AnnotationFactory.createAnnotation(jcas, 0, 5, WordListDescription.class).setName("A");
		AnnotationFactory.createAnnotation(jcas, 10, 18, WordListDescription.class).setName("B");
		AnnotationFactory.createAnnotation(jcas, 20, 21, WordListDescription.class).setName("C");
		AnnotationFactory.createAnnotation(jcas, 30, 30, WordListDescription.class).setName("A");
		AnnotationFactory.createAnnotation(jcas, 32, 42, WordListDescription.class).setName("A");
		AnnotationFactory.createAnnotation(jcas, 41, 45, WordListDescription.class).setName("B");
	}

	@Test
	public void testExport() {
		MarkdownExporter mdex = new MarkdownExporter();
		mdex.clazz = WordListDescription.class;
		mdex.featureName = "Name";
		mdex.type = WordListDescription.class.getName();

		String str = mdex.getMarkdownString(jcas);

		System.err.println(str);
		assertEquals('[', str.charAt(0));
		assertEquals("[Lorem]~A~", str.substring(0, 10));
	}
}

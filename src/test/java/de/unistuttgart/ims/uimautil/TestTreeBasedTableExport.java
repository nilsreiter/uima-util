package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.uimautil.TreeBasedTableExport.MissingValueBehaviour;
import de.unistuttgart.ims.uimautil.TreeBasedTableExport.Tree;
import de.unistuttgart.ims.uimautil.api.TestMeta1;
import de.unistuttgart.ims.uimautil.api.TestMeta2;
import de.unistuttgart.ims.uimautil.api.TestSegment;
import de.unistuttgart.ims.uimautil.api.TestType;

public class TestTreeBasedTableExport {
	JCas jcas;
	Configuration conf;

	Type testTypeType, tokenType;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();

		testTypeType = jcas.getTypeSystem().getType(TestType.class.getName());
		tokenType = jcas.getTypeSystem().getType(Token.class.getName());

		jcas.setDocumentText(
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.");
		Token[] tokens = new Token[] { AnnotationFactory.createAnnotation(jcas, 0, 5, Token.class),
				AnnotationFactory.createAnnotation(jcas, 6, 11, Token.class),
				AnnotationFactory.createAnnotation(jcas, 22, 25, Token.class) };
		POS[] poss = new POS[] { AnnotationFactory.createAnnotation(jcas, 0, 5, POS.class),
				AnnotationFactory.createAnnotation(jcas, 6, 11, POS.class) };
		poss[0].setPosValue("A");
		poss[1].setPosValue("B");
		tokens[0].setPos(poss[0]);
		tokens[1].setPos(poss[1]);
		TestSegment ts = AnnotationFactory.createAnnotation(jcas, 0, 20, TestSegment.class);
		ts.setArrayFeature(new FSArray(jcas, 2));
		ts.setArrayFeature(0, AnnotationFactory.createAnnotation(jcas, 0, 2, TestType.class));
		ts.setArrayFeature(1, AnnotationFactory.createAnnotation(jcas, 5, 7, TestType.class));
		ts = AnnotationFactory.createAnnotation(jcas, 21, 30, TestSegment.class);
		conf = new INIConfiguration();
	}

	@Test
	public void testTreePopulation() {
		conf.addProperty("de..tudarmstadt..ukp..dkpro..core..api..segmentation..type..Token.xpaths",
				"begin,end,pos/PosValue,coveredText()");
		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addAnnotationType(TestSegment.class);
		tbte.addAnnotationType(Token.class);
		Tree<FeatureStructure> t = tbte.populateTree(jcas);
		assertNotNull(t);
		assertNotNull(t.getPayload());
		assertEquals(DocumentAnnotation.class, t.getPayload().getClass());
		assertEquals(2, t.size());
		assertEquals(2, t.getChild(0).size());
		assertEquals(3, t.height());
	}

	@Test
	public void testNonAnnotations() {
		TestMeta1 tm = new TestMeta1(jcas);
		tm.setMyFeature("v1");
		tm.addToIndexes();

		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addAnnotationType(TestMeta1.class);

		Tree<FeatureStructure> tree = tbte.getFullTree(jcas);
		assertNotNull(tree);
		assertEquals(1, tree.size());
		assertEquals(2, tree.height());
		assertEquals(TestMeta1.class, tree.getChild(0).getPayload().getClass());
		assertEquals("v1", ((TestMeta1) tree.getChild(0).getPayload()).getMyFeature());

		// adding a second feature structure of the same type
		tm = new TestMeta1(jcas);
		tm.setMyFeature("v2");
		tm.addToIndexes();

		tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addAnnotationType(TestMeta1.class);

		tree = tbte.getFullTree(jcas);
		assertNotNull(tree);
		assertEquals(2, tree.size());
		assertEquals(2, tree.height());
		assertEquals(TestMeta1.class, tree.getChild(0).getPayload().getClass());
		assertEquals(TestMeta1.class, tree.getChild(1).getPayload().getClass());
		assertEquals("v1", ((TestMeta1) tree.getChild(0).getPayload()).getMyFeature());
		assertEquals("v2", ((TestMeta1) tree.getChild(1).getPayload()).getMyFeature());

		TestMeta2 tm2 = new TestMeta2(jcas);
		tm2.setMyFeature("v3");
		tm2.addToIndexes();
		tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addAnnotationType(TestMeta1.class);
		tbte.addAnnotationType(TestMeta2.class);
		tree = tbte.getFullTree(jcas);

		System.out.println(tree);
	}

	@Test
	public void testTreeExtension() {

		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addPathEntry(Token.class, "begin,end,pos/PosValue,coveredText()");
		tbte.addArrayEntry(TestSegment.class, "ArrayFeature");
		tbte.addAnnotationType(TestSegment.class);
		tbte.addAnnotationType(Token.class);
		Tree<FeatureStructure> t = tbte.populateTree(jcas);
		t = tbte.extendArrays(t);
		// System.out.println(t);
		assertNotNull(t);
		assertNotNull(t.getPayload());
		assertEquals(DocumentAnnotation.class, t.getPayload().getClass());
		assertEquals(2, t.size());
		assertEquals(4, t.height());

		t = t.getChild(0);
		assertEquals(2, t.size());
		assertEquals(TestSegment.class, t.getPayload().getClass());

		t = t.getChild(1);
		assertEquals(2, t.size());
		assertEquals(TestType.class, t.getPayload().getClass());
	}

	@Test
	public void testNoCoveredAnnotations() {
		AnnotationFactory.createAnnotation(jcas, 31, 35, TestSegment.class);

		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addPathEntry(Token.class, "begin,end,pos/PosValue,coveredText()");
		tbte.addPathEntry(TestSegment.class, "begin,end");

		tbte.addAnnotationType(TestSegment.class);
		tbte.addAnnotationType(Token.class);
		List<List<Object>> l = tbte.convert(jcas, false);
		assertEquals(4, l.size());

		tbte.setMissingValueBehaviour(MissingValueBehaviour.OMIT);
		l = tbte.convert(jcas, false);
		assertEquals(3, l.size());

	}

	@Test
	public void testAdditionalFeatures() {
		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addAnnotationType(TestType.class);
		tbte.addExportFeatures(testTypeType, "MyFeature");
		List<List<Object>> r = tbte.convert(jcas, false);
		assertEquals(2, r.size());
		assertEquals(1, r.get(0).size());

	}

	public void testOutput() {
		conf.addProperty("de..tudarmstadt..ukp..dkpro..core..api..segmentation..type..Token.xpaths",
				"begin,end,pos/PosValue,coveredText()");
		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addAnnotationType(TestSegment.class);
		tbte.addAnnotationType(Token.class);
		Tree<FeatureStructure> t = tbte.populateTree(jcas);

		System.out.println(t);
		List<List<Object>> table = new LinkedList<List<Object>>();
		tbte.flatten(new LinkedList<Object>(), t, table, 0);
		System.out.println(table);
		t = tbte.extendArrays(t);
		System.out.println(t);

		tbte.flatten(new LinkedList<Object>(), t, table, 0);
	}
}

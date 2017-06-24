package de.unistuttgart.ims.uimautil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.uimautil.TreeBasedTableExport.Tree;
import de.unistuttgart.ims.uimautil.api.TestSegment;
import de.unistuttgart.ims.uimautil.api.TestType;

public class TestTreeBasedTableExport {
	JCas jcas;
	Configuration conf;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createJCas();
		jcas.setDocumentText(
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.");
		Token[] tokens = new Token[] { AnnotationFactory.createAnnotation(jcas, 0, 5, Token.class),
				AnnotationFactory.createAnnotation(jcas, 6, 11, Token.class) };
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
		conf = new INIConfiguration();
	}

	@Test
	public void testTreePopulation() {
		conf.addProperty("de..tudarmstadt..ukp..dkpro..core..api..segmentation..type..Token.xpaths",
				"begin,end,pos/PosValue,coveredText()");
		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addTypeToExport(TestSegment.class);
		tbte.addTypeToExport(Token.class);
		Tree<FeatureStructure> t = tbte.populateTree(jcas);
		assertNotNull(t);
		assertEquals(1, t.getChildren().size());
		assertEquals(2, t.getChild(0).getChildren().size());

	}

	public void testOutput() {
		conf.addProperty("de..tudarmstadt..ukp..dkpro..core..api..segmentation..type..Token.xpaths",
				"begin,end,pos/PosValue,coveredText()");
		TreeBasedTableExport tbte = new TreeBasedTableExport(conf, jcas.getTypeSystem());
		tbte.addTypeToExport(TestSegment.class);
		tbte.addTypeToExport(Token.class);
		Tree<FeatureStructure> t = tbte.populateTree(jcas);

		System.out.println(t);
		tbte.addArrayFeatureToPrint(JCasUtil.getType(jcas, TestSegment.class).getFeatureByBaseName("ArrayFeature"));
		tbte.flatten(new LinkedList<Object>(), t);
		t = tbte.extendArrays(t);
		System.out.println(t);

		tbte.flatten(new LinkedList<Object>(), t);
	}
}

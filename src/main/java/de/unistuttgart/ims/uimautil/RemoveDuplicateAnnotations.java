package de.unistuttgart.ims.uimautil;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public class RemoveDuplicateAnnotations extends JCasAnnotator_ImplBase {
	public static final String PARAM_TYPE = "Type";

	@ConfigurationParameter(name = PARAM_TYPE)
	String type = null;
	Class<? extends Annotation> clazz;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		Class<?> cl;
		try {
			cl = Class.forName(type);
		} catch (final ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}

		clazz = (Class<? extends Annotation>) cl;

	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Set<IntPair> s = new HashSet<IntPair>();
		Set<Annotation> toRemove = new HashSet<Annotation>();
		for (Annotation a : JCasUtil.select(jcas, clazz)) {
			IntPair ip = new IntPair(a.getBegin(), a.getEnd());
			if (s.contains(ip)) {
				toRemove.add(a);
			} else {
				s.add(ip);
			}
		}

		for (Annotation a : toRemove) {
			a.removeFromIndexes();
		}
	}

	class IntPair {
		// Ideally, name the class after whatever you're actually using
		// the int pairs *for.*
		final int x;
		final int y;

		IntPair(int x, int y) {
			this.x = x;
			this.y = y;
		}
		// depending on your use case, equals? hashCode? More methods?

		@Override
		public boolean equals(Object o) {
			if (o instanceof IntPair) {
				IntPair p = (IntPair) o;
				return (x == p.x && y == p.y);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return x * y;

		}

	}
}

package de.unistuttgart.ims.uimautil;

import java.io.IOException;
import java.io.PrintStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;

public class MarkdownExporter extends JCasFileWriter_ImplBase {

	public static final String PARAM_TYPE = "Type to Export";
	public static final String PARAM_FEATURE = "Feature to subscript";
	public static final String PARAM_BEGIN_MARK = "Begin Mark";
	public static final String PARAM_END_MARK = "End Mark";
	public static final String PARAM_DOUBLE_NEWLINE = "Double Newline";

	@ConfigurationParameter(name = PARAM_TYPE)
	String type = null;
	Class<? extends Annotation> clazz;

	@ConfigurationParameter(name = PARAM_FEATURE)
	String featureName = null;

	@ConfigurationParameter(name = PARAM_BEGIN_MARK, mandatory = false, defaultValue = "[")
	String beginMark = "[";

	@ConfigurationParameter(name = PARAM_END_MARK, mandatory = false, defaultValue = "]")
	String endMark = "]";

	@ConfigurationParameter(name = PARAM_DOUBLE_NEWLINE, mandatory = false, defaultValue = "false")
	boolean doubleNewline = false;

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

		NamedOutputStream aw = null;
		PrintStream ps = null;
		try {
			aw = getOutputStream(jcas, ".md");
			ps = new PrintStream(aw);
			ps.print(getMarkdownString(jcas));
			ps.flush();
			aw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(ps);
			IOUtils.closeQuietly(aw);
		}
	}

	public String getMarkdownString(JCas jcas) {
		Feature feat = jcas.getTypeSystem().getType(type).getFeatureByBaseName(featureName);

		SortedSet<Insertion> insertions = new TreeSet<Insertion>();
		for (Annotation a : JCasUtil.select(jcas, clazz)) {
			insertions.add(new Insertion(beginMark, a.getBegin()));
			insertions.add(new Insertion(endMark + "~" + a.getFeatureValueAsString(feat) + "~", a.getEnd()));
		}

		StringBuilder exportString = new StringBuilder(jcas.getDocumentText());
		for (Insertion ins : insertions) {
			exportString.insert(ins.position, ins.content);
		}

		if (doubleNewline)
			return exportString.toString().replaceAll("\n", "\n\n");
		else
			return exportString.toString().replaceAll("\n", "  \n");
	}

	static class Insertion implements Comparable<Insertion> {
		public Insertion(String content, int position) {
			super();
			this.content = content;
			this.position = position;
		}

		String content;
		int position;

		public int compareTo(Insertion o) {
			if (this.position == o.position)
				return -1;
			return -Integer.compare(this.position, o.position);
		}

	}
}

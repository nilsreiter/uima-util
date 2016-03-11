package de.unistuttgart.ims.uimautil;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationUtil {

	static Pattern pattern = null;

	/**
	 * trims the annotated text. Similar to {@link String#trim()}, this method
	 * moves the begin and end indexes towards the middle as long as there is
	 * whitespace.
	 * 
	 * The method throws a ArrayIndexOutOfBoundsException if the entire
	 * annotation consists of whitespace.
	 * 
	 * @param annotation
	 *            The annotation to trim
	 * @param ws
	 *            An array of chars that are to be considered whitespace
	 * @return The trimmed annotation (not a copy)
	 */
	public static <T extends Annotation> T trim(T annotation, char... ws) {
		char[] s = annotation.getCoveredText().toCharArray();
		if (s.length == 0) return annotation;

		int b = 0;
		while (ArrayUtils.contains(ws, s[b])) {
			b++;
		}

		int e = 0;
		while (ArrayUtils.contains(ws, s[(s.length - 1) - e])) {
			e++;
		}
		annotation.setBegin(annotation.getBegin() + b);
		annotation.setEnd(annotation.getEnd() - e);
		return annotation;
	}

	/**
	 * 
	 * 
	 * This method first checks whether the string contains whitespace at
	 * all.
	 * 
	 * @see {@link #trim(Annotation, char...) }
	 * @param annotation
	 *            The annotation to trim
	 * @return
	 */
	public static <T extends Annotation> T trim(T annotation) {
		if (pattern == null) {
			pattern = Pattern.compile("\\S");
		}
		if (!pattern.matcher(annotation.getCoveredText()).find()) {
			return annotation;
		}
		return trim(annotation, ' ', '\n', '\t', '\r', '\f');
	}

	/**
	 * trims an entire collection of annotations. Beware: directly trimming the
	 * result of {@link JCasUtil#select()} throws a
	 * {@link ConcurrentModificationException}.
	 * 
	 * @param annotations
	 *            The annotations you want to trim
	 */
	public static <T extends Annotation> void trim(Collection<T> annotations) {
		for (T anno : annotations) {
			trim(anno, ' ', '\n', '\t', '\r', '\f');
		}
	}

	/**
	 * Moves the begin-index as long as a character contain in the array is at
	 * the beginning.
	 * 
	 * @param annotation
	 * @param ws
	 * @return
	 */
	public static <T extends Annotation> T trimFront(T annotation, char... ws) {
		char[] s = annotation.getCoveredText().toCharArray();
		if (s.length == 0) return annotation;

		int b = 0;
		while (ArrayUtils.contains(ws, s[b])) {
			b++;
		}

		annotation.setBegin(annotation.getBegin() + b);
		return annotation;
	}
}

package de.unistuttgart.ims.uimautil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.jcas.tcas.Annotation;

public class IMSUtil {
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
	 * @see #trim(Annotation, char...).
	 * @param annotation
	 *            The annotation to trim
	 * @return
	 */
	public static <T extends Annotation> T trim(T annotation) {
		return trim(annotation, ' ', '\n', '\t', '\r', '\f');
	}
}

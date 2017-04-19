package de.unistuttgart.ims.uimautil;

import org.apache.commons.lang.ClassUtils;
import org.apache.uima.resource.ResourceInitializationException;

public class TypeParameterUtil {
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClass(String name) throws ResourceInitializationException {
		try {
			Class<?> r = ClassUtils.getClass(name);
			return (Class<T>) r;
		} catch (ClassNotFoundException e) {
			throw new ResourceInitializationException(e);
		}
	}
}

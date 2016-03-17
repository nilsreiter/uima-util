package de.unistuttgart.ims.uimautil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;

public class JSONExport extends JCasFileWriter_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		final JSONObject object = new JSONObject();

		object.put("text", jcas.getDocumentText());
		object.put("language", jcas.getDocumentLanguage());
		final JSONObject annotationList = new JSONObject();

		final AnnotationIndex<Annotation> annoIndex = jcas.getAnnotationIndex();
		final Iterator<Annotation> iter = annoIndex.iterator();
		while (iter.hasNext()) {
			final Annotation anno = iter.next();
			final Type type = anno.getType();
			final JSONObject jsonAnnotation = new JSONObject();
			// jsonAnnotation.put("type", type.getName());
			for (final Feature feature : type.getFeatures()) {
				if (feature.getRange().isPrimitive())
					jsonAnnotation.put(feature.getShortName(), anno.getFeatureValueAsString(feature));
			}

			annotationList.append(type.getName(), jsonAnnotation);
		}
		object.put("annotations", annotationList);

		// writing the output
		OutputStream os = null;
		OutputStreamWriter osw = null;
		try {
			os = this.getOutputStream(jcas, ".json");
			osw = new OutputStreamWriter(os);
			osw.write(object.toString());
			osw.flush();
		} catch (final IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(osw);
			IOUtils.closeQuietly(os);
		}
	}

}

package de.unistuttgart.ims.uimautil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.uimautil.export.MyFeaturePathColumn;

public class TreeBasedTableExport {
	Configuration configuration;

	List<Class<? extends TOP>> annotationTypes = new LinkedList<Class<? extends TOP>>();

	TypeSystem typeSystem;
	List<Object> header = new LinkedList<Object>();
	boolean headerDone = false;

	public TreeBasedTableExport(Configuration config, TypeSystem ts) {
		typeSystem = ts;
		configuration = config;
	}

	protected void addPathEntry(Class<? extends TOP> type, String paths) {
		configuration.addProperty(type.getCanonicalName().replaceAll("\\.", "..") + ".paths", paths);
	}

	protected void addArrayEntry(Class<? extends TOP> type, String featureNames) {
		configuration.addProperty(type.getCanonicalName().replaceAll("\\.", "..") + ".arraymulti", featureNames);

	}

	protected Tree<FeatureStructure> getFullTree(JCas jcas) {
		Tree<FeatureStructure> tree = populateTree(jcas);
		tree = extendCoverings(tree);
		tree = extendArrays(tree);
		return tree;
	}

	public synchronized List<List<Object>> convert(JCas jcas, boolean newHeader) {
		Tree<FeatureStructure> tree = getFullTree(jcas);
		if (newHeader) {
			headerDone = false;
			header = new LinkedList<Object>();
		}
		List<List<Object>> table = new LinkedList<List<Object>>();

		flatten(new LinkedList<Object>(), tree, table, 0);
		if (newHeader)
			table.add(0, header);
		return table;
	}

	protected List<Object> getColumns(FeatureStructure fs, int treelevel) {
		List<Object> r = new LinkedList<Object>();

		String[] paths = getUnaryFeaturePathsForType(fs.getType());
		String[] labels = getColumnHeadersForType(fs.getType());
		for (int i = 0; i < paths.length; i++) {
			MyFeaturePathColumn pcol = new MyFeaturePathColumn(paths[i]);
			r.add(pcol.getValue(fs));
			if (!headerDone) {
				String l;
				if (labels.length > i)
					l = labels[i];
				else
					l = paths[i];
				header.add((treelevel > 1 ? getHeaderPrefixForType(fs.getType()) + "." : "") + l);
			}
		}

		return r;

	}

	protected String getHeaderPrefixForType(Type type) {

		return configuration.getString("typeNameMapping." + type.getName().replaceAll("\\.", ".."),
				type.getShortName());
	}

	private String[] getColumnHeadersForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".labels";
		String confEntry = configuration.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

	private String[] getUnaryFeaturePathsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".paths";
		String confEntry = configuration.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

	private String[] getCoveringsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".covered";
		String confEntry = configuration.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];
	}

	private String[] getArrayMultiplierForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".arraymulti";
		String confEntry = configuration.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];
	}

	protected void flatten(List<Object> history, Tree<FeatureStructure> tree, List<List<Object>> table, int treelevel) {
		history.addAll(getColumns(tree.getPayload(), treelevel));
		if (tree.isLeaf()) {
			table.add(history);
			headerDone = true;
		} else {
			for (Tree<FeatureStructure> child : tree.getChildren()) {
				flatten(new LinkedList<Object>(history), child, table, treelevel + 1);
			}
		}
	}

	protected Tree<FeatureStructure> extendArrays(Tree<FeatureStructure> tree) {
		Type type = tree.getPayload().getType();

		String[] aFeatures = getArrayMultiplierForType(type);
		for (String s : aFeatures) {
			Feature feature = type.getFeatureByBaseName(s);
			List<Tree<FeatureStructure>> children = tree.getChildren();
			FSArray arr = (FSArray) tree.getPayload().getFeatureValue(feature);
			Type cType = feature.getRange().getComponentType();// arr.getType().getComponentType();
			if (arr == null || arr.size() == 0) {
				tree.setChildren(new ArrayList<Tree<FeatureStructure>>());
				FeatureStructure fs = tree.getPayload().getCAS().createFS(cType);
				tree.getPayload().getCAS().addFsToIndexes(fs);
				Tree<FeatureStructure> nChild = new Tree<FeatureStructure>(fs);
				nChild.setChildren(children);
				tree.add(nChild);
			} else {
				tree.setChildren(new ArrayList<Tree<FeatureStructure>>(arr.size()));
				for (int i = 0; i < arr.size(); i++) {
					Tree<FeatureStructure> nChild = new Tree<FeatureStructure>(arr.get(i));
					nChild.setChildren(children);
					tree.add(nChild);
				}

			}
		}

		for (Tree<FeatureStructure> child : tree.getChildren()) {
			extendArrays(child);
		}
		return tree;
	}

	protected Tree<FeatureStructure> extendCoverings(Tree<FeatureStructure> tree) {
		Type type = tree.getPayload().getType();
		String[] coverings = getCoveringsForType(type);
		for (String covType : coverings) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Annotation> annoClass = (Class<? extends Annotation>) Class.forName(covType);
				List<Tree<FeatureStructure>> children = tree.getChildren();
				List<? extends Annotation> annos = JCasUtil.selectCovered(annoClass, (Annotation) tree.getPayload());
				tree.setChildren(new ArrayList<Tree<FeatureStructure>>(annos.size()));

				for (Annotation a : annos) {
					Tree<FeatureStructure> nChild = new Tree<FeatureStructure>(a);
					nChild.setChildren(children);
					tree.add(nChild);
				}
				if (annos.isEmpty()) {
					FeatureStructure fs = tree.getPayload().getCAS().createFS(typeSystem.getType(covType));
					tree.getPayload().getCAS().addFsToIndexes(fs);
					Tree<FeatureStructure> nChild = new Tree<FeatureStructure>(fs);
					nChild.setChildren(children);
					tree.add(nChild);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		for (Tree<FeatureStructure> child : tree.getChildren()) {
			extendCoverings(child);
		}
		return tree;
	}

	/**
	 * This is the entry point for Tree construction. The root is the document
	 * node.
	 * 
	 * @param jcas
	 *            The document
	 * @return A tree of feature structures
	 */
	protected Tree<FeatureStructure> populateTree(JCas jcas) {
		Tree<FeatureStructure> tree = new Tree<FeatureStructure>(jcas.getDocumentAnnotationFs());
		for (TOP a : JCasUtil.select(jcas, annotationTypes.get(0))) {
			tree.add(populateTree(jcas, a, 1));
		}
		return tree;

	}

	/**
	 * This function recursively constructs a tree containing the feature
	 * structures that are covering each other. Longer annotations should be on
	 * higher levels of the tree.
	 * 
	 * 
	 * @param jcas
	 *            The document
	 * @param anno
	 *            The current annotation
	 * @param typeIndex
	 *            An index referring to the list of annotation types to export
	 * @return A Tree with FeatureStructures as payloads
	 */
	@SuppressWarnings("unchecked")
	protected Tree<FeatureStructure> populateTree(JCas jcas, TOP anno, int typeIndex) {
		Tree<FeatureStructure> tree = new Tree<FeatureStructure>(anno);
		if (annotationTypes.size() > typeIndex)
			if (anno instanceof Annotation) {
				Class<? extends Annotation> rAnno = (Class<? extends Annotation>) annotationTypes.get(typeIndex);
				for (TOP a : JCasUtil.selectCovered((Class<? extends Annotation>) rAnno, (Annotation) anno)) {
					tree.add(populateTree(jcas, a, typeIndex + 1));
				}
			} else {
				for (TOP a : JCasUtil.select(jcas, annotationTypes.get(typeIndex))) {
					tree.add(populateTree(jcas, a, typeIndex + 1));
				}
			}
		return tree;
	}

	static class Tree<T> {
		T payload;
		List<Tree<T>> children = new ArrayList<Tree<T>>();

		public Tree(T col) {
			payload = col;
		}

		public T getPayload() {
			return payload;
		}

		public void setPayload(T column) {
			this.payload = column;
		}

		public Tree<T> getChild(int i) {
			return this.getChildren().get(i);
		}

		public List<Tree<T>> getChildren() {
			return children;
		}

		public void setChildren(List<Tree<T>> children) {
			this.children = children;
		}

		public boolean add(Tree<T> e) {
			return children.add(e);
		}

		public boolean add(T e) {
			return children.add(new Tree<T>(e));
		}

		public boolean isLeaf() {
			return children.isEmpty();
		}

		@Override
		public String toString() {
			return toString(0);
		}

		public String toString(int indent) {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < indent; i++)
				b.append(" ");
			b.append(StringEscapeUtils.ESCAPE_JAVA.translate(payload.toString())).append("\n");
			for (Tree<T> child : children) {
				b.append(child.toString(indent + 3));
			}
			return b.toString();
		}

		public int height() {
			if (children.isEmpty()) {
				return 1;
			} else {
				return children.get(0).height() + 1;
			}
		}

		public int size() {
			return children.size();
		}
	}

	public List<Class<? extends TOP>> getTypesToExport() {
		return annotationTypes;
	}

	public void setAnnotationTypes(List<Class<? extends TOP>> typesToExport) {
		this.annotationTypes = typesToExport;
	}

	public boolean addAnnotationType(Class<? extends TOP> type) {
		return annotationTypes.add(type);
	}
}

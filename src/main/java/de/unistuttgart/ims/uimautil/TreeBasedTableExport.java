package de.unistuttgart.ims.uimautil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.uimautil.export.MyFeaturePathColumn;

public class TreeBasedTableExport {
	Configuration configuration;

	List<Class<? extends Annotation>> typesToExport = new LinkedList<Class<? extends Annotation>>();

	Set<Feature> primitiveFeaturesToPrint = new HashSet<Feature>();
	Set<Feature> arrayFeaturesToPrint = new HashSet<Feature>();

	TypeSystem typeSystem;

	public TreeBasedTableExport(Configuration config, TypeSystem ts) {
		typeSystem = ts;
		configuration = config;
	}

	public List<List<Object>> export(JCas jcas) {
		Tree<FeatureStructure> tree = populateTree(jcas);
		tree = extendArrays(tree);
		List<List<Object>> table = new LinkedList<List<Object>>();
		flatten(new LinkedList<Object>(), tree, table);
		return table;
	}

	protected List<Object> getColumns(FeatureStructure fs) {
		List<Object> r = new LinkedList<Object>();

		String[] paths = getUnaryFeaturePathsForType(fs.getType());

		for (String path : paths) {
			MyFeaturePathColumn pcol = new MyFeaturePathColumn(path);
			r.add(pcol.getValue(fs));
		}

		return r;

	}

	private String[] getUnaryFeaturePathsForType(Type type) {
		String confKey = type.getName().replaceAll("\\.", "..") + ".xpaths";
		String confEntry = configuration.getString(confKey, null);
		if (confEntry != null && !confEntry.isEmpty())
			return confEntry.split(",");
		else
			return new String[0];

	}

	protected void flatten(List<Object> history, Tree<FeatureStructure> tree, List<List<Object>> table) {
		history.addAll(getColumns(tree.getPayload()));
		if (tree.isLeaf()) {
			table.add(history);
		} else {
			for (Tree<FeatureStructure> child : tree.getChildren()) {
				flatten(new LinkedList<Object>(history), child, table);
			}
		}
	}

	protected Tree<FeatureStructure> extendArrays(Tree<FeatureStructure> tree) {
		Type type = tree.getPayload().getType();
		for (Feature feature : type.getFeatures()) {
			if (arrayFeaturesToPrint.contains(feature)) {
				List<Tree<FeatureStructure>> children = tree.getChildren();
				FSArray arr = (FSArray) tree.getPayload().getFeatureValue(feature);
				tree.setChildren(new ArrayList<Tree<FeatureStructure>>(arr.size()));
				for (int i = 0; i < arr.size(); i++) {
					Tree<FeatureStructure> nChild = new Tree<FeatureStructure>(arr.get(i));
					nChild.setChildren(children);
					tree.add(nChild);
				}
				break;
			}
		}
		for (Tree<FeatureStructure> child : tree.getChildren()) {
			extendArrays(child);
		}
		return tree;
	}

	protected Tree<FeatureStructure> populateTree(JCas jcas) {
		Tree<FeatureStructure> tree = new Tree<FeatureStructure>(jcas.getDocumentAnnotationFs());
		for (Annotation a : JCasUtil.select(jcas, typesToExport.get(0))) {
			tree.add(populateTree(jcas, a, 1));
		}
		return tree;

	}

	protected Tree<FeatureStructure> populateTree(JCas jcas, Annotation anno, int typeIndex) {
		Tree<FeatureStructure> tree = new Tree<FeatureStructure>(anno);
		if (typesToExport.size() > typeIndex)
			for (Annotation a : JCasUtil.selectCovered(typesToExport.get(typeIndex), anno)) {
				tree.add(populateTree(jcas, a, typeIndex + 1));
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
	}

	public List<Class<? extends Annotation>> getTypesToExport() {
		return typesToExport;
	}

	public void setTypesToExport(List<Class<? extends Annotation>> typesToExport) {
		this.typesToExport = typesToExport;
	}

	public Set<Feature> getPrimitiveFeaturesToPrint() {
		return primitiveFeaturesToPrint;
	}

	public void setPrimitiveFeaturesToPrint(Set<Feature> primitiveFeaturesToPrint) {
		this.primitiveFeaturesToPrint = primitiveFeaturesToPrint;
	}

	public Set<Feature> getArrayFeaturesToPrint() {
		return arrayFeaturesToPrint;
	}

	public void setArrayFeaturesToPrint(Set<Feature> arrayFeaturesToPrint) {
		this.arrayFeaturesToPrint = arrayFeaturesToPrint;
	}

	public boolean addArrayFeatureToPrint(Feature e) {
		return arrayFeaturesToPrint.add(e);
	}

	public boolean addPrimitiveFeatureToPrint(Feature e) {
		return primitiveFeaturesToPrint.add(e);
	}

	public boolean addArrayFeatureToPrint(String e) {
		return arrayFeaturesToPrint.add(typeSystem.getFeatureByFullName(e));
	}

	public boolean addPrimitiveFeatureToPrint(String e) {
		return primitiveFeaturesToPrint.add(typeSystem.getFeatureByFullName(e));
	}

	public boolean addTypeToExport(Class<? extends Annotation> type) {
		return typesToExport.add(type);
	}
}

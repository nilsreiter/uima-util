package de.unistuttgart.ims.uimautil.trie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TrieNode<T> {
	// private TrieNode<T> parent;
	private List<TrieNode<T>> children;
	private boolean isLeaf; // Quick way to check if any children exist
	private boolean isWord; // Does this node represent the last character
							// of a word
	private T character; // The character this node represents

	/**
	 * Constructor for top level root node.
	 */
	public TrieNode() {
		children = new LinkedList<TrieNode<T>>();
		isLeaf = true;
		isWord = false;
	}

	/**
	 * Constructor for child node.
	 */
	public TrieNode(T character) {
		this();
		this.character = character;
	}

	/**
	 * Adds a word to this node. This method is called recursively and adds
	 * child nodes for each successive letter in the word, therefore recursive
	 * calls will be made with partial words.
	 * 
	 * @param word
	 *            the word to add
	 */
	protected void addWord(List<T> word) {
		isLeaf = false;

		TrieNode<T> node = getNode(word.get(0));

		if (node == null) {
			node = new TrieNode<T>(word.get(0));
			// node.parent = this;
			children.add(node);
		}

		if (word.size() > 1) {
			node.addWord(word.subList(1, word.size()));
		} else {
			node.isWord = true;
		}
	}

	/**
	 * Returns the child TrieNode representing the given char, or null if no
	 * node exists.
	 * 
	 * @param c
	 * @return
	 */
	protected TrieNode<T> getNode(T c) {
		for (int i = 0; i < children.size(); i++) {
			TrieNode<T> ch = children.get(i);
			if (ch.character.equals(c))
				return ch;
		}
		return null;
	}

	/**
	 * Returns a List of String objects which are lower in the hierarchy that
	 * this node.
	 * 
	 * @return
	 */
	protected List<List<T>> getWords() {
		// Create a list to return
		List<List<T>> list = new ArrayList<List<T>>();

		// If this node represents a word, add it
		if (isWord) {
			List<T> l = new ArrayList<T>();
			l.add(character);
			list.add(l);
		}

		// If any children
		if (!isLeaf) {
			// Add any words belonging to any children
			for (int i = 0; i < children.size(); i++) {
				List<List<T>> suffixes = children.get(i).getWords();
				for (List<T> l : suffixes) {
					l.add(0, character);
					list.add(l);
				}
			}
		}
		return list;
	}

	@Override
	public String toString() {
		return character.toString();
	}

}
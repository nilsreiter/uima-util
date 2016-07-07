package de.unistuttgart.ims.uimautil.trie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Deprecated
public class Trie<T> {
	private TrieNode<T> root;

	/**
	 * Constructor
	 */
	public Trie(Comparator<T> cmp) {
		root = new TrieNode<T>(cmp);
	}

	/**
	 * Constructor
	 */
	public Trie() {
		root = new TrieNode<T>(null);
	}

	/**
	 * Adds a word to the Trie
	 * 
	 * @param word
	 */
	public void addWord(List<T> word) {
		root.addWord(word);
	}

	/**
	 * Get the words in the Trie with the given prefix
	 * 
	 * @param prefix
	 * @return a List containing String objects containing the words in the Trie
	 *         with the given prefix.
	 */
	public List<List<T>> getWords(List<T> prefix) {
		// Find the node which represents the last letter of the prefix
		TrieNode<T> lastNode = root;
		for (int i = 0; i < prefix.size(); i++) {
			lastNode = lastNode.getNode(prefix.get(i));

		}
		// If no node matches, then no words exist, return empty list
		if (lastNode == null)
			return new ArrayList<List<T>>();

		// Return the words which eminate from the last node
		List<List<T>> r = lastNode.getWords();
		for (List<T> l : r) {
			l.addAll(0, prefix.subList(0, prefix.size() - 1));
		}
		return r;
	}
}
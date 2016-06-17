package de.unistuttgart.ims.uimautil.trie;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestTrie {
	@Test
	public void testTrie() {
		Trie<Integer> iTrie = new Trie<Integer>();

		iTrie.addWord(Arrays.asList(1, 2, 1, 3, 1, 1));
		iTrie.addWord(Arrays.asList(2, 1, 3, 1, 1));
		iTrie.addWord(Arrays.asList(1, 3, 1, 1));
		System.out.println(iTrie.getWords(Arrays.asList(1)));

		Trie<String> trie = new Trie<String>();
		List<String> text = Arrays.asList("the", "dog", "bark", "the", "cat", "bark");
		for (int i = 0; i < text.size(); i++)
			trie.addWord(text.subList(i, text.size()));
		System.out.println(trie.getWords(Arrays.asList("the")));
	}
}

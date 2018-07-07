package seach.data_structure.tree;

import java.util.List;

public interface TrieTree<T> {

	/*
	 * insert str into trie tree with value t
	 */
	void insert(String str, T t);
	
	/*
	 * get value from trie tree with index str
	 */
	T search(String str);
	
	/*
	 * get value list from trie tree using prefix search
	 */
	List<T> prefixSearch(String prefix);
}

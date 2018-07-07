package seach.data_structure.tree.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import seach.data_structure.tree.TrieTree;

public class TableBaseTrieTree<T> implements TrieTree<T>{
	
	private List<Node> states = new ArrayList<>();
	
	public TableBaseTrieTree() {
		Node root = new Node();
		this.states.add(root);
	}
	

	@Override
	public void insert(String str, T t) {
		if(str != null && str.length() > 0) {
			byte[] bs = str.getBytes();
			int parent = 0;
			for(int i = 0; i < bs.length; i++) {
				parent = this.insert(parent, bs[i]);
				if(i == bs.length - 1) {
					this.states.get(parent).setT(t);
				}
			}
		}
		
	}
	
	private int insert(int parent, byte b) {
		List<Integer> parentNext = this.states.get(parent).getNext();
		Integer child = parentNext.get(b + 127);
		if(child == null) {
			Node childNode = new Node();
			this.states.add(childNode);
			child =  this.states.size() - 1;
			parentNext.set(b + 127, child);
		}
		return child;
	}

	@Override
	public T search(String str) {
		if(str != null && str.length() > 0) {
			byte[] bs = str.getBytes();
			int parent = 0;
			for(int i = 0; i < bs.length; i++) {
				parent = this.search(parent, bs[i]);
			}
			if(parent >= 0 && parent < this.states.size()) {
				return this.states.get(parent).getT();
			}
		}
		return null;
	}
	
	private int search(int parent, byte b) {
		if(parent >= 0 && parent < this.states.size()) {
			List<Integer> parentNext = this.states.get(parent).getNext();
			Integer child = parentNext.get(b + 127);
			if(child != null) {
				return child;
			}
		}
		return -1;
	}

	@Override
	public List<T> prefixSearch(String prefix) {
		List<T> result = new LinkedList<>();
		if(prefix != null && prefix.length() > 0) {
			byte[] bs = prefix.getBytes();
			int parent = 0;
			for(int i = 0; i < bs.length; i++) {
				parent = this.search(parent, bs[i]);
			}
			if(parent >= 0 && parent < this.states.size()) {
				List<Integer> q = new LinkedList<>();
				q.add(parent);
				while(q.size() > 0) {
					int head = q.remove(0);
					T t = this.states.get(head).getT();
					if(t != null) {
						result.add(t);
					}
					q.addAll(this.states
							.get(head)
							.getNext()
							.stream()
							.filter( e -> e != null)
							.collect(Collectors.toList()));
				}
			}
		}
		return result;
	}
	
	
	private class Node{
		private T t;
		private List<Integer> next = new ArrayList<>();
		
		public Node() {
			for(int i = 0; i < 256; i++) {
				this.next.add(null);
			}
		}

		public T getT() {
			return t;
		}

		public void setT(T t) {
			this.t = t;
		}

		public List<Integer> getNext() {
			return next;
		}
		
		
	}

	public static void main(String[] args) {
		Map<String, Integer> dict = new HashMap<>();
		dict.put("江南", 1);
		dict.put("江南大学", 2);
		dict.put("江边", 3);
		dict.put("云南", 4);
		dict.put("南方", 6);
		
		TrieTree<Integer> tree1 = new TableBaseTrieTree<>();
		for(Entry<String, Integer> entry : dict.entrySet()) {
			tree1.insert(entry.getKey(), entry.getValue());
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree1.search(key));
		}
		
		System.out.println("江南：" + tree1.prefixSearch("江南"));
		
		TrieTree<String> tree2 = new TableBaseTrieTree<>();
		for(String key: dict.keySet()) {
			tree2.insert(key, key);
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree2.search(key));
		}
		
		System.out.println("江南：" + tree2.prefixSearch("江南"));
	}
}

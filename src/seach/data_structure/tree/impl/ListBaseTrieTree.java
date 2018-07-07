package seach.data_structure.tree.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import seach.data_structure.tree.TrieTree;

public class ListBaseTrieTree<T> implements TrieTree<T>{
	
	private Node root;
	
	public ListBaseTrieTree() {
		this.root = new Node();
	}

	@Override
	public void insert(String str, T t) {
		if(str != null && str.length() > 0) {
			byte[] bs = str.getBytes();
			Node parent = this.root;
			for(int i = 0; i < bs.length; i++) {
				parent = this.insert(parent, bs[i]);
				if(i == bs.length - 1) {
					parent.setT(t);
				}
			}
		}
	}
	
	
	private Node insert(Node parent, byte b) {
		Node child = parent.getNext().get(b + 127);
		if(child == null) {
			child = new Node();
			parent.getNext().set(b + 127, child);
		}
		return child;
	}

	@Override
	public T search(String str) {
		if(str != null && str.length() > 0) {
			byte[] bs = str.getBytes();
			Node parent = this.root;
			for(int i = 0; i < bs.length; i++) {
				parent = this.search(parent, bs[i]);
			}
			if(parent != null) {
				return parent.getT();
			}
		}
		return null;
	}
	
	private Node search(Node parent, byte b) {
		if(parent != null) {
			return parent.getNext().get(b + 127);
		}
		return null;
	}

	@Override
	public List<T> prefixSearch(String prefix) {
		List<T> results = new LinkedList<>();
		if(prefix != null && prefix.length() > 0) {
			byte[] bs = prefix.getBytes();
			Node parent = this.root;
			for(int i = 0; i < bs.length; i++) {
				parent = this.search(parent, bs[i]);
			}
			if(parent != null) {
				List<Node> q = new LinkedList<>();
				q.add(parent);
				while(q.size() > 0) {
					Node head = q.remove(0);
					T t = head.getT();
					if(t != null) {
						results.add(t);
					}
					q.addAll(head.getNext()
							.stream()
							.filter(e -> e != null)
							.collect(Collectors.toList()));
				}
			}
		}
		return results;
	}
	
	private class Node{
		private T t;
		private List<Node> next = new ArrayList<>();
		
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

		public List<Node> getNext() {
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
		
		TrieTree<Integer> tree1 = new ListBaseTrieTree<>();
		for(Entry<String, Integer> entry : dict.entrySet()) {
			tree1.insert(entry.getKey(), entry.getValue());
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree1.search(key));
		}
		
		System.out.println("江南：" + tree1.prefixSearch("江南"));
		
		TrieTree<String> tree2 = new ListBaseTrieTree<>();
		for(String key: dict.keySet()) {
			tree2.insert(key, key);
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree2.search(key));
		}
		
		System.out.println("江南：" + tree2.prefixSearch("江南"));
	}
}

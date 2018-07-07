package seach.data_structure.tree.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import seach.data_structure.tree.TrieTree;

public class DoubleArrayTrieTree<T> implements TrieTree<T>{
	
	private List<Node> states = new ArrayList<>();
	private List<Integer> checks = new ArrayList<>(); 
	
	public DoubleArrayTrieTree() {
		Node root = new Node();
		this.states.add(root);
	}

	@Override
	public void insert(String str, T t) {
		if(str != null && str.length() > 0) {
			byte[] bs = str.getBytes();
			int parent = 0;
			for(byte b : bs) {
				parent = this.insert(parent, b);
			}
			this.states.get(parent).setT(t);;
		}
	}
	
	private int insert(int parent, byte b) {
		Node parentNode = this.states.get(parent);
		int parentBase = parentNode.getBase();
		int tempChildIndex = parentBase + b;
		ensureSize(this.states, tempChildIndex);
		ensureSize(this.checks, tempChildIndex);
		Integer checkValue = this.checks.get(tempChildIndex);
		if(checkValue == null) {
			this.checks.set(tempChildIndex, parent);
			this.states.set(tempChildIndex, new Node());
		}else if (checkValue != parent) {
			int maxIndex = Integer.MIN_VALUE;
			List<Integer> children = new LinkedList<>();
			for(int i = 0; i < this.checks.size(); i++) {
				Integer childIndex = this.checks.get(i);
				if(childIndex != null && childIndex == parent) {
					children.add(i);
					if(i > maxIndex) {
						maxIndex = i;
					}
				}
			}
			children.add(tempChildIndex);
			if(tempChildIndex > maxIndex) {
				maxIndex = tempChildIndex;
			}
			int offset = this.ensureCheckValid(children, maxIndex);
			parentNode.setBase(parentBase + offset);
			for(int i = 0; i < children.size() - 1; i++) {
				int originalIndex = children.get(i);
				this.checks.set(originalIndex + offset, parent);
				this.states.set(originalIndex + offset, this.states.get(originalIndex));
				for(int j = 0; j < this.checks.size(); j++) {
					Integer originalCheckValue = this.checks.get(j);
					if(originalCheckValue != null && originalCheckValue == originalIndex) {
						this.checks.set(j, originalIndex + offset);
					}
				}
				this.checks.set(originalIndex, null);
			}
			tempChildIndex = tempChildIndex + offset;
			this.checks.set(tempChildIndex, parent);
			this.states.set(tempChildIndex, new Node());
		}
		return tempChildIndex;
	}
	
	
	private void ensureSize(List<?> list, int size) {
		int originalSize = list.size() - 1;
		if(size > originalSize) {
			for(int i = 0; i < size - originalSize; i ++) {
				list.add(null);
			}
		}
	}
	
	private int ensureCheckValid(List<Integer> list, int maxIndex) {
		int offset = 0;
		while(true) {
			int lastIndex = maxIndex + offset;
			ensureSize(this.checks, lastIndex);
			ensureSize(this.states, lastIndex);
			if(this.checkValid(list, offset)) {
				break;
			}
			offset++;
		}
		return offset;
	}
	
	
	private boolean checkValid(List<Integer> list, int offset) {
		for(int originalIndex : list) {
			if(this.checks.get(originalIndex + offset) != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public T search(String str) {
		if(str != null && str.length() > 0) {
			byte[] bs = str.getBytes();
			int parent = 0;
			for(byte b : bs) {
				parent = this.search(parent, b);
			}
			if(parent >= 0) {
				Node node = this.states.get(parent);
				if(node != null) {
					return node.getT();
				}
			}
		}
		return null;
	}
	
	private int search(int parent, byte b) {
		if(parent >= 0 && parent < this.states.size()) {
			Node parentNode =  this.states.get(parent);
			int childIndex = parentNode.getBase() + b;
			Integer checkValue = this.checks.get(childIndex);
			if(checkValue != null && checkValue == parent) {
				return childIndex;
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
			if(parent > 0) {
				List<Integer> q = new LinkedList<>();
				q.add(parent);
				while (q.size() > 0) {
					int head = q.remove(0);
					T t = this.states.get(head).getT();
					if(t != null) {
						result.add(t);
					}
					for(int i = 0; i < this.checks.size(); i++) {
						Integer checkValue = this.checks.get(i);
						if(checkValue != null && checkValue == head) {
							q.add(i);
						}
					}
				}
			}
		}
		return result;
	}
	
	private class Node{
		
		private int base = 128;
		private T t;
		
		public Node() {
			// TODO Auto-generated constructor stub
		}

		public int getBase() {
			return base;
		}

		public void setBase(int base) {
			this.base = base;
		}

		public T getT() {
			return t;
		}

		public void setT(T t) {
			this.t = t;
		}
		
		@Override
		public String toString() {
			return "base:" + base + "t:" + t;
		}
	}

	public static void main(String[] args) {
		Map<String, Integer> dict = new HashMap<>();
		dict.put("江南", 1);
		dict.put("江南大学", 2);
		dict.put("江边", 3);
		dict.put("云南", 4);
		dict.put("南方", 6);
		
	
		TrieTree<Integer> tree1 = new DoubleArrayTrieTree<>();
		for(Entry<String, Integer> entry : dict.entrySet()) {
			tree1.insert(entry.getKey(), entry.getValue());
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree1.search(key));
		}
		
		System.out.println("江南：" + tree1.prefixSearch("江南"));
		
		TrieTree<String> tree2 = new DoubleArrayTrieTree<>();
		for(String key: dict.keySet()) {
			tree2.insert(key, key);
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree2.search(key));
		}
		
		System.out.println("江南：" + tree2.prefixSearch("江南"));
	}
}

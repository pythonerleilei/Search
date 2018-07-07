package seach.data_structure.tree.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import seach.data_structure.tree.TrieTree;

public class TripleArrayTrieTree<T> implements TrieTree<T>{
	
	private List<Node> states =  new ArrayList<>();
	private List<Integer> next = new ArrayList<>();
	private List<Integer> check = new ArrayList<>();
	
	public TripleArrayTrieTree() {
		Node root = new Node();
		root.setBase(128);
		this.states.add(root);
	}
	
	
	public void insert(String str, T t) {
		if(str != null && str.length() > 0) {
			byte[] bs = str.getBytes();
			int stateIndex = 0;
			for(int i = 0; i < bs.length; i ++) {
				stateIndex = this.insert(stateIndex, bs[i]);
				if(i == bs.length - 1) {
					this.states.get(stateIndex).setT(t);
				}
			}
		}
	}
	
	
	private int insert(int stateIndex, byte b) {
		int stateBase = this.states.get(stateIndex).getBase();
		int nextIndex = stateBase + b;
		ensureSize(this.check, nextIndex);
		ensureSize(this.next, nextIndex);
		Integer parentIndex = this.check.get(nextIndex);
		
		if(parentIndex == null) {
			this.check.set(nextIndex, stateIndex);
			Node state = new Node();
			state.setBase(128);
			this.states.add(state);
			int nextStatIndex = this.states.size() - 1;
			this.next.set(nextIndex, nextStatIndex);
		}else if(parentIndex != stateIndex) {
				List<Integer> childIndexs = new LinkedList<>();
				int maxIndex = Integer.MIN_VALUE;
				for(int i = 0; i < this.check.size(); i++) {
					Integer checkValue = this.check.get(i);
					if(checkValue != null && checkValue == stateIndex) {
						childIndexs.add(i);
						maxIndex = i > maxIndex ? i : maxIndex;
					}
				}
				maxIndex = nextIndex > maxIndex ? nextIndex : maxIndex;
				childIndexs.add(nextIndex);
				int offset = this.ensureCheckValid(childIndexs, maxIndex);
				this.states.get(stateIndex).setBase(stateBase + offset);
				for(int i = 0; i < childIndexs.size() - 1; i++) {
					int originalIndex = childIndexs.get(i);
					this.check.set(originalIndex + offset, stateIndex);
					this.next.set(originalIndex + offset, this.next.get(originalIndex));
					this.check.set(originalIndex, null);
				}
				nextIndex = nextIndex + offset;
				this.check.set(nextIndex, stateIndex);
				Node state = new Node();
				state.setBase(128);
				this.states.add(state);
				int nextStatIndex = this.states.size() - 1;
				this.next.set(nextIndex, nextStatIndex);	
		}
		
		return this.next.get(nextIndex);
	}
	
	private int ensureCheckValid(List<Integer> list, int maxIndex) {
		int offset = 0;
		while(true) {
			int lastIndex = maxIndex + offset;
			ensureSize(this.check, lastIndex);
			ensureSize(this.next, lastIndex);
			if(this.checkValid(list, offset)) {
				break;
			}
			offset++;
		}
		return offset;
	}
	
	
	private boolean checkValid(List<Integer> list, int offset) {
		for(int originalIndex : list) {
			if(this.check.get(originalIndex + offset) != null) {
				return false;
			}
		}
		return true;
	}
	
	
	public T search(String str) {
		if(str != null && str.length() > 0) {
			int stateIndex = 0;
			byte[] bs = str.getBytes();
			for(int i = 0; i < bs.length; i++) {
				stateIndex = this.search(stateIndex, bs[i]);
			}
			if(stateIndex > 0) {
				return this.states.get(stateIndex).getT();
			}
		}
		return null;
	}
	
	
	private int search(int stateIndex, byte b){
		if(stateIndex >= 0 && stateIndex < this.states.size()) {
			int nextIndex = this.states.get(stateIndex).getBase() + b;
			if(nextIndex < this.check.size() && this.check.get(nextIndex) == stateIndex) {
				return this.next.get(nextIndex);
			}
		}
		return -1;
	}
	
	private void ensureSize(List<?> list, int size) {
		int originalSize = list.size() - 1;
		if(size > originalSize) {
			for(int i = 0; i < size - originalSize; i ++) {
				list.add(null);
			}
		}
	}
	
	@Override
	public List<T> prefixSearch(String prefix) {
		List<T> result = new LinkedList<>();
		if(prefix != null && prefix.length() > 0) {
			byte[] bs = prefix.getBytes();
			int stateIndex = 0;
			for(int i = 0; i < bs.length; i++) {
				stateIndex = this.search(stateIndex, bs[i]);
			}
			if(stateIndex > 0) {
				List<Integer> q = new LinkedList<>();
				q.add(stateIndex);
				while (q.size() > 0) {
					int head = q.remove(0);
					T t = this.states.get(head).getT();
					if(t != null) {
						result.add(t);
					}
					q.addAll(this.getChildren(head));
				}
			}
		}
		return result;
	}
	
	private List<Integer> getChildren(int parent){
		List<Integer> children = new LinkedList<>();
		for(int i = 0; i < this.check.size(); i++) {
			Integer checkValue = this.check.get(i);
			if(checkValue != null && checkValue == parent) {
				children.add(this.next.get(i));
			}
		}
		return children;
	}
	
	private class Node{
	
		private Integer base;
		private T t;
		
		public Node() {
			// TODO Auto-generated constructor stub
		}
		
		public T getT() {
			return t;
		}
		
		public void setT(T t) {
			this.t = t;
		}
		
		public Integer getBase() {
			return base;
		}
		public void setBase(Integer base) {
			this.base = base;
		}
		
	}

	
	public static void main(String[] args) throws UnsupportedEncodingException {
		Map<String, Integer> dict = new HashMap<>();
		dict.put("江南", 1);
		dict.put("江南大学", 2);
		dict.put("江边", 3);
		dict.put("云南", 4);
		dict.put("南方", 6);
		
		TrieTree<Integer> tree1 = new TripleArrayTrieTree<>();
		for(Entry<String, Integer> entry : dict.entrySet()) {
			tree1.insert(entry.getKey(), entry.getValue());
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree1.search(key));
		}
		
		System.out.println("江南：" + tree1.prefixSearch("江南"));
		
		TrieTree<String> tree2 = new TripleArrayTrieTree<>();
		for(String key: dict.keySet()) {
			tree2.insert(key, key);
		}
		
		for(String key: dict.keySet()) {
			System.out.println(key + ":" + tree2.search(key));
		}
		
		System.out.println("江南：" + tree2.prefixSearch("江南"));
	}
}

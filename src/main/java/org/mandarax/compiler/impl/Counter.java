package org.mandarax.compiler.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple counter utility used for code generation.
 * @author jens dietrich
 */
public class Counter {
	private int v = 0;
	public int getValue() {
		return v;
	}
	public int getNext() {
		v=v+1;
		return this.v;
	}
	
	private Map<String,Integer> values = new HashMap<String,Integer>();
	public int getValue(String id) {
		Integer v = values.get(id);
		if (v==null) {
			return -1;
		}
		else {
			return v;
		}
	}
	public int getNext(String id) {
		Integer last = values.get(id);
		if (last==null) {
			last = new Integer(-1);
		}
		int next = last+1;
		values.put(id,next);
		return next;
	}
	public void reset(String id) {
		values.remove(id);
	}
	public void resetAll() {
		values.clear();
	}
}

package org.mandarax.compiler.impl;
/**
 * Represents a variable with type information attached to it.
 * @author jens dietrich
 */
public class TypedVariable {
	private String name = null;
	private String type = null;
	
	public TypedVariable(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}

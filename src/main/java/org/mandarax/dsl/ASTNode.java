package org.mandarax.dsl;

import java.util.*;

/**
 * Superclass for all AST nodes.
 * @author jens dietrich
 */

public abstract class ASTNode  implements Visitable {
	public ASTNode(Position position,Context context) {
		super();
		this.position = position;
		this.context = context;
	}

	// the position of this artefact in the script defining it
	private Position position = null;
	// contextual information
	private Context context = null;
	// cached variables
	private List<Variable> variables = null;
	// additional properties
	private Map<Object,Object> properties = new HashMap<Object,Object>(); 
	
	public void setProperty(Object key,Object value) {
		properties.put(key, value);
	}
	public Object getProperty(Object key) {
		return properties.get(key);
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	// utility for recursive printing
	protected void appendList(List<? extends Object> objs, StringBuffer b, boolean br,String sep) {
		if (br) b.append('(');
		boolean f = true;
		for (Object obj:objs) {
			if (f) f=false;
			else b.append(sep);
			b.append(obj);
		}
		if (br) b.append(')');
	}
	
	/**
	 * Get all variables contained in this node.
	 * @return
	 */
	public List<Variable> getVariables() {
		// try cached variables first
		if (variables!=null) return variables;
		
		class VariableCollector extends AbstractASTVisitor {
			List<Variable> variables = new ArrayList<Variable>();
			@Override
			public boolean visit(Variable x) {
				if (!variables.contains(x)) variables.add(x);
				return super.visit(x);
			}
		}
		VariableCollector collector = new VariableCollector();
		this.accept(collector);
		variables = collector.variables;
		
		return variables;
		
	}
	
	// reset cached info
	public void reset() {
		this.variables = null;
	}

	protected void copyPropertiesTo(ASTNode node) {
		for (Map.Entry<?,?> e:this.properties.entrySet()) {
			node.setProperty(e.getKey(),e.getValue());
		}
	}
}

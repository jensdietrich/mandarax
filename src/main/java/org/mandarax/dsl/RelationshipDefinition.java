
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Represents relationship definitions. 
 * @author jens dietrich
 */
public class RelationshipDefinition extends AnnotatableNode {
	
	private String name = null;
	private List<FunctionDeclaration> queries = null;
	private List<VariableDeclaration> slotDeclarations = null;
	private List<String> superTypes = null;
	private List<RelationshipDefinitionPart> rules = new ArrayList<RelationshipDefinitionPart>();
	// if true, slots have type information that can be used by the compiler
	// if false, this is a generic relationship and casts must be generated
	private boolean typeSafe = true;
	
	/**
	 * Constructor.
	 * @throws InternalScriptException thrown if method parameter names do not occur in slot definitions
	 */
	public RelationshipDefinition(Position position, Context context, String name,List<VariableDeclaration> slotDeclarations,List<String> superTypes,List<FunctionDeclaration> queries) throws InternalScriptException {
		super(position, context);
		this.name = name;
		this.slotDeclarations = slotDeclarations;
		this.superTypes = superTypes;
		this.queries = queries;
		
		// set backreference for queries
		for (FunctionDeclaration query:queries) {
			query.setRelationship(this);
		}
		
		// consistency check: all methodParamNames must occur in the slot declarations
		Collection<String> definedNames = Collections2.transform(slotDeclarations,new Function<VariableDeclaration,String>(){
			@Override
			public String apply(VariableDeclaration v) {return v.getName();}});
		
		
		for (FunctionDeclaration query:queries) {
			for (String refedName:query.getParameterNames()) {
				if (!definedNames.contains(refedName)) 
					throw new InternalScriptException("Exception in query definition at " + query.getPosition() + " the following method parameter is not defined as predicate slot: " + name);
			}
		}
	}


	
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			for (FunctionDeclaration f:this.queries) f.accept(visitor);
			for (VariableDeclaration v:this.slotDeclarations) v.accept(visitor);
			for (RelationshipDefinitionPart r:this.rules) r.accept(visitor);
		}
		visitor.endVisit(this);
	}

	public String getName() {
		return name;
	}

	public List<VariableDeclaration> getSlotDeclarations() {
		return slotDeclarations;
	}

	public List<FunctionDeclaration> getQueries() {
		return queries;
	}
	
	public FunctionDeclaration getQuery(boolean[] signature) {
		List<String> paramNames = new ArrayList<String>();
		for (int i=0;i<signature.length;i++) {
			if (signature[i]) paramNames.add(this.getSlotDeclarations().get(i).getName());
		}
		
		for (FunctionDeclaration q:getQueries()) {
			List<String> paramNames2 = q.getParameterNames();
			boolean eq = paramNames.size()==paramNames2.size();
			if (eq) {
				for (int i=0;i<paramNames.size();i++) {
					eq = eq && paramNames.get(i).equals(paramNames2.get(i));
				}
			}
			if (eq) return q;
		}
		return null;
	}
	
	private FunctionDeclaration getQuery(String name,int slotNumber) {
		for (FunctionDeclaration q:getQueries()) {
			if (name.equals(q.getName()) && slotNumber==q.getParameterNames().size()) return q;
		}
		return null;
	}
	
	public FunctionDeclaration getQuery(FunctionInvocation inv) {
		return getQuery(inv.getFunction(),inv.getParameters().size());
	}

	public List<String> getSuperTypes() {
		return superTypes;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(name);
		appendList(slotDeclarations, b,true,",");
		if (superTypes!=null && !superTypes.isEmpty()) {
			b.append("extends ");
			this.appendList(superTypes, b,false,",");
		}
		b.append(' ');
		appendList(queries, b,false,",");
		return b.toString();
	}


	public List<RelationshipDefinitionPart> getDefinitionParts() {
		return Collections.unmodifiableList(rules);
	}
	
	public List<Rule> getRules() {
		List<Rule> l = new ArrayList<Rule>(rules.size());
		for (Object part:rules) {
			if (part instanceof Rule) {
				l.add((Rule)part);
			}
		}
		return Collections.unmodifiableList(l);
	}
	
	
	public void addRule(Rule r) throws InternalScriptException {
		FunctionInvocation f = r.getHead();
		
		// check - predicate name in rule head must match name of relationship
		if (!f.getFunction().equals(name)) {
			throw new InternalScriptException("Error at " + r.getPosition() + ", the rule defines a predicate " + f.getFunction() + " but this does not match the name of the relationship that is to be defined (" + this.getName() + ")" );
		}
		
		// check - number of parameters must match
		if (f.getParameters().size()!=this.getSlotDeclarations().size()) {
			throw new InternalScriptException("Error at " + r.getPosition() + ", the rule defines a predicate with " + f.getParameters().size() + " slots but this does not match the number of slots of the relationship that is to be defined (" + this.getSlotDeclarations().size() + ")" );
		}
		
		rules.add(r);
	}
	
	public void addExternal(ExternalFacts r) throws InternalScriptException {
		rules.add(r);
	}
	
	public String getTypeNameForSlot(String slotName) {
		for (VariableDeclaration varDecl:slotDeclarations) {
			if (varDecl.getName().equals(slotName)) return varDecl.getType();
		}
		return null;
	}
	
	public int getSlotPosition(String slotName) {
		for (int i=0;i<this.slotDeclarations.size();i++) {
			if (this.slotDeclarations.get(i).getName().equals(slotName)) {
				return i;
			}
		}
		throw new IllegalArgumentException("There is no slot named " + slotName + " defined in " + this);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((queries == null) ? 0 : queries.hashCode());
		result = prime * result + ((rules == null) ? 0 : rules.hashCode());
		result = prime
				* result
				+ ((slotDeclarations == null) ? 0 : slotDeclarations.hashCode());
		result = prime * result
				+ ((superTypes == null) ? 0 : superTypes.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationshipDefinition other = (RelationshipDefinition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (queries == null) {
			if (other.queries != null)
				return false;
		} else if (!queries.equals(other.queries))
			return false;
		if (rules == null) {
			if (other.rules != null)
				return false;
		} else if (!rules.equals(other.rules))
			return false;
		if (slotDeclarations == null) {
			if (other.slotDeclarations != null)
				return false;
		} else if (!slotDeclarations.equals(other.slotDeclarations))
			return false;
		if (superTypes == null) {
			if (other.superTypes != null)
				return false;
		} else if (!superTypes.equals(other.superTypes))
			return false;
		return true;
	}



	public boolean isTypeSafe() {
		return typeSafe;
	}



	public void setTypeSafe(boolean typeSafe) {
		this.typeSafe = typeSafe;
	}



	public void setDefinitionPart(int i, RelationshipDefinitionPart defPart) {
		this.rules.set(i,defPart);
	}
	

}

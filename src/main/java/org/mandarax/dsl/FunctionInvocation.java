
package org.mandarax.dsl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import static org.mandarax.dsl.Utils.*;

/**
 * Function invocation. A function can refer either to a relationship query or is imported.
 * Note that this information is not set by the parser, references must be resolved (usually by the compiler) in a separate processing step. 
 * @author jens dietrich
 */
public class FunctionInvocation extends Expression {

	private String function = null;
	private List<Expression> parameters = new ArrayList<Expression>();
	private RelationshipDefinition relationship = null; // if this is a reference to a relationship
	private Method referencedMethod = null; // if this is a reference to an imported function
	private boolean naf = false; // negation as failure can only be true if relationship != null
	private boolean builtInPredicate = false;
	
	private static Logger LOGGER = Logger.getLogger(FunctionInvocation.class);
	
	public FunctionInvocation(Position position,Context context,String function,List<Expression> parameters) {
		super(position,context);
		this.function = function;
		this.parameters = parameters;
	}
	
	public static FunctionInvocation createInBuildIn(Position position,Context context,Expression var,Expression container) {
		List<Expression> parameters = new ArrayList<Expression>(2);
		parameters.add(var);
		parameters.add(container);
		FunctionInvocation f = new FunctionInvocation(position,context,"_InDomain",parameters);
		f.builtInPredicate = true;
		return f;
	}

	public boolean isDefinedByRelationship() {
		return relationship!=null;
	}
	
	public String getFunction() {
		return function;
	}
	
	@Override
	public Expression substitute(final Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			FunctionInvocation e = new FunctionInvocation(getPosition(),getContext(),function,transformList(parameters, new Function<Expression,Expression>() {
				@Override
				public Expression apply(Expression p) {
					return p.substitute(substitutions);
				}}));
			e.setRelationship(this.getRelationship());
			e.setReferencedMethod(this.getReferencedMethod());
			e.setType(this.getType());
			e.setNaf(this.naf);
			e.builtInPredicate = this.builtInPredicate;
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}



	public List<Expression> getParameters() {
		return parameters;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			for (Expression param:parameters) {
				param.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((function == null) ? 0 : function.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
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
		FunctionInvocation other = (FunctionInvocation) obj;
		if (function == null) {
			if (other.function != null)
				return false;
		} else if (!function.equals(other.function))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}


	public void setFunction(String function) {
		this.function = function;
	}

	public Method getReferencedMethod() {
		return referencedMethod;
	}

	public void setReferencedMethod(Method referencedMethod) {
		this.referencedMethod = referencedMethod;
	}

	public RelationshipDefinition getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationshipDefinition relationship) {
		this.relationship = relationship;
	}
	
	@Override
	public List<Expression> getChildren() {
		List<Expression> children = new ArrayList<Expression>();
		children.addAll(this.parameters);
		return children;
	}

	public boolean isNaf() {
		return naf;
	}

	public void setNaf(boolean naf) {
		this.naf = naf;
	}
	
	@Override
	public String toString() {
		if (naf) return "not " + super.toString();
		else return super.toString();
	}

	public boolean isBuiltInPredicate() {
		return builtInPredicate;
	}

}

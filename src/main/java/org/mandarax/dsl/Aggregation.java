
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an aggregation. 
 * @author jens dietrich
 */
public class Aggregation extends Expression {

	private AggregationFunction function = null;
	private Variable variable = null;
	private FunctionInvocation expression = null;
	
	
	public Aggregation(Position position,Context context,String fun,String var,FunctionInvocation expr) {
		super(position,context);
		this.function = AggregationFunction.valueOf(fun);
		this.variable = new Variable(position,context,var);
		this.expression = expr;
	}
	
	private Aggregation(Position position,Context context,AggregationFunction fun,Variable var,FunctionInvocation expr) {
		super(position,context);
		this.function = fun;
		this.variable = var;
		this.expression = expr;
	}

	
	@Override
	public Expression substitute(final Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			Aggregation e = new Aggregation(getPosition(),getContext(),function,(Variable)variable.substitute(substitutions),(FunctionInvocation)expression.substitute(substitutions));
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}

	@Override
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			variable.accept(visitor);
			expression.accept(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public List<Expression> getChildren() {
		List<Expression> children = new ArrayList<Expression>(2);
		children.add(variable);
		children.add(expression);
		return children;
	}

	public AggregationFunction getFunction() {
		return function;
	}

	public Variable getVariable() {
		return variable;
	}

	public FunctionInvocation getExpression() {
		return expression;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((function == null) ? 0 : function.hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
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
		Aggregation other = (Aggregation) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (function != other.function)
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}

	/**
	 * Indicates whether this expression is constructed from a list of given expressions. 
	 * @param boundExpressions
	 * @return
	 */
	public boolean isGroundWRT(Collection<Expression> boundExpressions) {
		// as we quantify over the variable in the aggregation, we can add it to bound expressions
		Set<Expression> boundExpr = new HashSet<Expression>();
		boundExpr.addAll(boundExpressions);
		boundExpr.add(this.variable);
		return this.expression.isGroundWRT(boundExpr);
	}

}

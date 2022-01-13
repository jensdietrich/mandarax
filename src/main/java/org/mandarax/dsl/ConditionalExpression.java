package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Conditional expression (cond?ifTrue:ifFalse).
 * @author jens dietrich
 */

public class ConditionalExpression extends Expression {
	
	private Expression condition = null;
	private Expression ifTrue = null;
	private Expression ifFalse = null;
	
	public ConditionalExpression(Position position,Context context,Expression condition, Expression ifTrue,Expression ifFalse) {
		super(position,context);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}
	public Expression getCondition() {
		return condition;
	}
	public void setCondition(Expression condition) {
		this.condition = condition;
	}
	public Expression getIfTrue() {
		return ifTrue;
	}
	public void setIfTrue(Expression ifTrue) {
		this.ifTrue = ifTrue;
	}
	public Expression getIfFalse() {
		return ifFalse;
	}
	public void setIfFalse(Expression ifFalse) {
		this.ifFalse = ifFalse;
	}
	
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			condition.accept(visitor);
			ifTrue.accept(visitor);
			ifFalse.accept(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((ifFalse == null) ? 0 : ifFalse.hashCode());
		result = prime * result + ((ifTrue == null) ? 0 : ifTrue.hashCode());
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
		ConditionalExpression other = (ConditionalExpression) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (ifFalse == null) {
			if (other.ifFalse != null)
				return false;
		} else if (!ifFalse.equals(other.ifFalse))
			return false;
		if (ifTrue == null) {
			if (other.ifTrue != null)
				return false;
		} else if (!ifTrue.equals(other.ifTrue))
			return false;
		return true;
	}
	
	@Override
	public List<Expression> getChildren() {
		List<Expression> children = new ArrayList<Expression>(3);
		children.add(this.condition);
		children.add(this.ifTrue);
		children.add(this.ifFalse);
		return children;
	}
	
	@Override
	public Expression substitute(Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			ConditionalExpression e = new ConditionalExpression(getPosition(),getContext(),condition.substitute(substitutions),ifTrue.substitute(substitutions),ifFalse.substitute(substitutions));
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
	
}

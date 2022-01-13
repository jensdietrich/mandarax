
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Expression using an unary operator such as ! or -.
 * @author jens dietrich
 */

public class UnaryExpression extends Expression {

	private UnOp operator = null;
	private Expression part = null;
	
	public UnaryExpression(Position position,Context context,UnOp operator, Expression part) {
		super(position,context);
		this.operator = operator;
		this.part = part;
	}
	
	
	public UnOp getOperator() {
		return operator;
	}
	public Expression getPart() {
		return part;
	}
	
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			part.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((part == null) ? 0 : part.hashCode());
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
		UnaryExpression other = (UnaryExpression) obj;
		if (operator != other.operator)
			return false;
		if (part == null) {
			if (other.part != null)
				return false;
		} else if (!part.equals(other.part))
			return false;
		return true;
	}
	
	@Override
	public List<Expression> getChildren() {
		List<Expression> children = new ArrayList<Expression>(1);
		children.add(this.part);
		return children;
	}
	
	@Override
	public Expression substitute(Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			UnaryExpression e = new UnaryExpression(getPosition(),getContext(),operator,part.substitute(substitutions));
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
	
}

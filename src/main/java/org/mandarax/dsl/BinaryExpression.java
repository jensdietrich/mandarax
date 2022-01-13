
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Represents binary expressions having two children connected by boolean or arithmetic operators.
 * @author jens dietrich
 */

public class BinaryExpression extends Expression {
	public BinaryExpression(Position position,Context context,BinOp operator, Expression left, Expression right) {
		super(position,context);
		this.operator = operator;
		this.left = left;
		this.right = right;
	}
	private BinOp operator = null;
	private Expression left = null;
	private Expression right = null;
	public BinOp getOperator() {
		return operator;
	}
	public Expression getLeft() {
		return left;
	}
	public Expression getRight() {
		return right;
	}
	
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			left.accept(visitor);
			right.accept(visitor);
		}
		visitor.endVisit(this);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		BinaryExpression other = (BinaryExpression) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (operator != other.operator)
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	@Override
	public List<Expression> getChildren() {
		List<Expression> children = new ArrayList<Expression>(2);
		children.add(left);
		children.add(right);
		return children;
	}
	@Override
	public Expression substitute(Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			BinaryExpression e = new BinaryExpression(getPosition(),getContext(),operator,left.substitute(substitutions),right.substitute(substitutions));
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
	
	
}

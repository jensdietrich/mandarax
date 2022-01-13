
package org.mandarax.dsl;

import java.util.Map;

/**
 * Integer literal.
 * @author jens dietrich
 */

public class IntLiteral extends Literal<Integer> {
	private int value = 0;

	public IntLiteral(Position position,Context context,int value) {
		super(position,context);
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
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
		IntLiteral other = (IntLiteral) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	@Override
	public Expression substitute(Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			IntLiteral e = new IntLiteral(getPosition(),getContext(),value);
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
	
}

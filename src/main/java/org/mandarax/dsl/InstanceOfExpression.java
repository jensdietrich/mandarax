
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * InstanceOf expression.
 * @author jens dietrich
 */
public class InstanceOfExpression extends Expression{
	private Expression objectReference = null;
	private String type = null;
	
	public InstanceOfExpression(Position position,Context context,Expression objectReference, String type) {
		super(position,context);
		this.objectReference = objectReference;
		this.type = type;
	}

	public Expression getObjectReference() {
		return objectReference;
	}
	public String getTypeName() {
		return type;
	}
	
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			objectReference.accept(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((objectReference == null) ? 0 : objectReference.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		InstanceOfExpression other = (InstanceOfExpression) obj;
		if (objectReference == null) {
			if (other.objectReference != null)
				return false;
		} else if (!objectReference.equals(other.objectReference))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	@Override
	public List<Expression> getChildren() {
		List<Expression> children = new ArrayList<Expression>(1);
		children.add(this.objectReference);
		return children;
	}
	
	@Override
	public Expression substitute(final Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			InstanceOfExpression e = new InstanceOfExpression(getPosition(),getContext(),objectReference.substitute(substitutions),type);
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
}

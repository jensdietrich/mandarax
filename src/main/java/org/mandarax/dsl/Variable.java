
package org.mandarax.dsl;

import java.util.List;
import java.util.Map;
import static org.mandarax.dsl.Utils.*;

/**
 * Variable/object references by name.
 * @author jens dietrich
 */

public class Variable extends Expression {
	private String name = null;
	private boolean defined = false; // this flag will be set to true if the variable is defined by an object declaration


	public Variable(Position position,Context context,String name) {
		super(position,context);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public boolean isFlat() {
		return true;
	}
	
	@Override
	public List<Expression> getChildren() {
		return EMPTY_LIST;
	}
	
	@Override
	public Expression substitute(final Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			Variable e = new Variable(getPosition(),getContext(),name);
			e.setType(this.getType());
			e.setDefined(defined);
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}

	public boolean isDefined() {
		return defined;
	}

	public void setDefined(boolean constant) {
		this.defined = constant;
	}



	@Override
	public boolean isGround() {
		return this.isDefined();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	

}

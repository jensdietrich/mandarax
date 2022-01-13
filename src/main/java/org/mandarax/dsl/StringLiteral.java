
package org.mandarax.dsl;

import static org.apache.commons.lang.StringEscapeUtils.*;
import java.util.Map;

/**
 * String literal.
 * @author jens dietrich
 */

public class StringLiteral extends Literal<String> {
	private String value = null;

	public StringLiteral(Position position,Context context,String value) {
		super(position,context);
		this.value = unescapeJava(value);
	}

	public String getValue() {
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
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringLiteral other = (StringLiteral) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public Expression substitute(Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			StringLiteral e = new StringLiteral(getPosition(),getContext(),value);
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
}

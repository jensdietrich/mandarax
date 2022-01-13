
package org.mandarax.dsl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import static org.mandarax.dsl.Utils.*;

/**
 * Null value.
 * @author jens dietrich
 */

public class NullValue extends Expression {

	public NullValue(Position position,Context context) {
		super(position,context);
	}
	
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public boolean isFlat() {
		return true;
	}
	
	@Override
	public List<Expression> getChildren() {
		return EMPTY_LIST;
	}
	
	/**
	 * Indicates whether this expression is constructed from a list of given expressions. 
	 * @param boundExpressions
	 * @return
	 */
	public boolean isGroundWRT(Collection<Expression> boundExpressions) {
		return true;
	}
	
	@Override
	public Expression substitute(Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			NullValue e = new NullValue(getPosition(),getContext());
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
}

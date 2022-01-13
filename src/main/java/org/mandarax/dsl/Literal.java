
package org.mandarax.dsl;

import java.util.Collection;
import java.util.List;
import static org.mandarax.dsl.Utils.*;

/**
 * Abstract superclass for literals.
 * @author jens dietrich
 */

public abstract class Literal<T> extends Expression {
	
	public Literal(Position position,Context context) {
		super(position,context);
	}

	public abstract T getValue();
	
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
}


package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.base.Function;
import static org.mandarax.dsl.Utils.*;

/**
 * Represents external facts imported using an iterable.
 * @author jens dietrich
 */
public class ExternalFacts extends RelationshipDefinitionPart {
	
	private Expression iterable = null;
	private FunctionInvocation head = null;

	public ExternalFacts(Position position, Context context,String id,Expression iterable) {
		super(position, context,id);
		this.iterable = iterable;
	}
	
	
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			this.iterable.accept(visitor);
		}
		visitor.endVisit(this);
	}

	public Expression getIterable() {
		return iterable;
	}
	
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(id);
		b.append(": include ");
		b.append(iterable.toString());
		return b.toString();
	}
		
	@Override
	public ExternalFacts clone() {
		ExternalFacts r = new ExternalFacts(getPosition(),getContext(),id,iterable.substitute(NO_SUBTITUTIONS));
		r.copyPropertiesTo(this);
		return r;
	}
	
	public ExternalFacts substitute(final Map<Expression,? extends Expression> substitutions) {
		return this.clone();
	}

	
}

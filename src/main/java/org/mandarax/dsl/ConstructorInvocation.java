package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.base.Function;
import static org.mandarax.dsl.Utils.*;

/**
 * Constructor invocation.
 * @author jens dietrich
 */
public class ConstructorInvocation extends Expression {

	private String type = null;
	private List<Expression> parameters = new ArrayList<Expression>();
	
	public ConstructorInvocation(Position position,Context context,String type,List<Expression> parameters) {
		super(position,context);
		this.type = type;
		this.parameters = parameters;
	}

	public String getTypeName() {
		return type;
	}



	public List<Expression> getParameters() {
		return parameters;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			for (Expression param:parameters) {
				param.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
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
		ConstructorInvocation other = (ConstructorInvocation) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
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
		List<Expression> children = new ArrayList<Expression>();
		children.addAll(this.parameters);
		return children;
	}
	
	
	@Override
	public Expression substitute(final Map<Expression,? extends Expression> substitutions) {
		Expression substituteThis = substitutions.get(this);
		if (substituteThis==null) {
			ConstructorInvocation e = new ConstructorInvocation(getPosition(),getContext(),type,transformList(parameters, new Function<Expression,Expression>() {
				@Override
				public Expression apply(Expression p) {
					return p.substitute(substitutions);
				}}));
			e.setType(this.getType());
			copyPropertiesTo(e);
			return e;
		}
		else {
			return substituteThis;
		}
	}
}

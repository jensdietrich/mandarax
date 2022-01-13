
package org.mandarax.dsl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import static org.mandarax.dsl.Utils.*;
import com.google.common.base.Predicate;


/**
 * Superclass for expression AST nodes.
 * @author jens dietrich
 */

public abstract class Expression extends ASTNode implements Cloneable {
	
	
	
	private Class _type = null;

	public Expression(Position position,Context context) {
		super(position,context);
	}
	
	public boolean isFlat() {
		return false;
	}
	
	public Class getType() {
		return _type;
	}
	
	public String getTypeName() {
		return _type!=null?_type.getName():null;
	}
	
	public void setType(Class type) {
		this._type = type;
	}
	
	
	// reset cached info
	public void reset() {
		super.reset();
		this._type = null;
	}
	/**
	 * Get a collection of children - those are nested expressions.
	 * @return
	 */
	public abstract List<Expression> getChildren();
	
	/**
	 * Get a collection of ground children.
	 * @return
	 */
	public List<Expression> getGroundChildren() {
		List<Expression> children = getChildren();
		List<Expression> list = new ArrayList<Expression>(children.size());
		for (Expression child:children) {
			if (child.isGround()) {
				list.add(child);
			}
		}
		return list;
	}
	
	
	/**
	 * Apply term substitutions. This will replace parts of the expression tree. 
	 * The result of this operation is a new expression - this expression will not be changed.
	 * @param substitutions
	 * @return
	 */
	public abstract Expression substitute(Map<Expression,? extends Expression> substitutions) ;
	
	/**
	 * Indicates whether this expression is constructed from a list of given expressions. 
	 * @param boundExpressions
	 * @return
	 */
	public boolean isGroundWRT(Collection<Expression> boundExpressions) {
		if (isGround() || boundExpressions.contains(this)) return true;
		else {
			List<Expression> children = getChildren();
			if (children.isEmpty()) return false;
			for (Expression child:children) {
				if (!child.isGroundWRT(boundExpressions)) return false;
			}
			return true;
		}
	}
	
	/**
	 * Indicates whether this expression is ground (does not contain variables).
	 * @return
	 */
	public boolean isGround() {
		return getVariables().isEmpty();
	}
	
	/**
	 * Get all expression children of this node.
	 * @return
	 */
	public Collection<Expression> getAllChildren() {
		Predicate<Object> nullfilter = new Predicate<Object>() {
			@Override
			public boolean apply(Object arg0) {
				return true;
			}};
		return getAllChildren(nullfilter);
	}
	/**
	 * Get all expression children of this node matching a filter.
	 * @return
	 */
	public Collection<Expression> getAllChildren(final Predicate<? super Expression> filter) {
		
		class VariableCollector extends AbstractASTVisitor {
			Collection<Expression> children = new LinkedHashSet<Expression>();
			private boolean add(Expression x) {
				if (x!=Expression.this && filter.apply(x)) {
					children.add(x);
				}
				return true;
			}
			@Override
			public boolean visit(BinaryExpression x) {return add(x);}
			@Override
			public boolean visit(BooleanLiteral x) {return add(x);}
			@Override
			public boolean visit(CastExpression x) {return add(x);}
			@Override
			public boolean visit(ConditionalExpression x) {return add(x);}
			@Override
			public boolean visit(InstanceOfExpression x) {return add(x);}
			@Override
			public boolean visit(IntLiteral x) {return add(x);}
			@Override
			public boolean visit(DoubleLiteral x) {return add(x);}
			@Override
			public boolean visit(MemberAccess x) {return add(x);}
			@Override
			public boolean visit(StringLiteral x) {return add(x);}
			@Override
			public boolean visit(UnaryExpression x) {return add(x);}
			@Override
			public boolean visit(Variable x) {return add(x);}
			@Override
			public boolean visit(FunctionInvocation x) {return add(x);}
			@Override
			public boolean visit(ConstructorInvocation x) {return add(x);}
			@Override
			public boolean visit(NullValue x) {return add(x);}

		}
		VariableCollector collector = new VariableCollector();
		this.accept(collector);
		return collector.children;

		
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		ExpressionPrinter printer = new ExpressionPrinter(b);
		try {
			printer.print(this);
		} catch (IOException e) {
			e.printStackTrace();
			return super.toString();
		}
		return printer.getOut().toString();
	}
	
	@Override
	public Expression clone() {
		return substitute(NO_SUBTITUTIONS);
	}
	
	
}

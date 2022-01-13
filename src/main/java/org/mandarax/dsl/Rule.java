
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.base.Function;
import static org.mandarax.dsl.Utils.*;

/**
 * Represents a rule.
 * @author jens dietrich
 */
public class Rule extends RelationshipDefinitionPart {
	
	private List<Expression> body = null;
	private FunctionInvocation head = null;
	// variable renamings in the rule head, will be set by relationship definition e.g., in 
	// rel Father(MalePerson father,Person child) extends Parent queries getFather(child),isFather(father,child) {
	// rule1: Son(c,f) -> Father(f,c);}
	// has a mapping {c->child,f->father} 
	private Map<String,String> variableMappingsInHead = null;


	public Rule(Position position, Context context,String id,Expression body,FunctionInvocation head) {
		super(position, context,id);
		this.body = flatten(body);
		this.head = head;
		
		// check whether head is flat
//		for (Expression term:head.getParameters()) {
//			if (!term.isFlat()) {
//				throw new InternalScriptException("Only flat terms (variables and terms) are allowed in rule heads, but this rule is violated by " + term + " " + term.getPosition());
//			}
//		}
		
	}
	
	public Rule(Position position, Context context,String id,List<Expression> body,FunctionInvocation head) {
		super(position, context,id);
		this.body = body;
		this.head = head;
		
	}


	/**
	 * Flatten a conjunction.
	 * @param expr
	 * @return
	 */
	private List<Expression> flatten(Expression expr) {
		if (expr==null) {
			return new ArrayList<Expression>(0);
		}
		else if (expr instanceof BinaryExpression && ((BinaryExpression)expr).getOperator()==BinOp.AND) {
			BinaryExpression bexpr = (BinaryExpression)expr;
			List<Expression> list = new ArrayList<Expression>();
			list.addAll(flatten(bexpr.getLeft()));
			list.addAll(flatten(bexpr.getRight()));
			return list;
		}
		else {
			List<Expression> list = new ArrayList<Expression>(1);
			list.add(expr);
			return list;
		}
	}

	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			head.accept(visitor);
			for (Expression e:this.body) e.accept(visitor);
		}
		visitor.endVisit(this);
	}

	public List<Expression> getBody() {
		return body;
	}

	public FunctionInvocation getHead() {
		return head;
	}
	
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(id);
		b.append(": ");
		this.appendList(body, b,false," & ") ; 
		b.append(" -> ");
		b.append(head);
		b.append(';');
		return b.toString();
	}
	
	public boolean isFact() {
		return this.body==null || this.body.isEmpty();
	}

	public Map<String, String> getVariableMappingsInHead() {
		return variableMappingsInHead;
	}

	public void setVariableMappingsInHead(Map<String, String> variableMappingsInHead) {
		this.variableMappingsInHead = variableMappingsInHead;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Rule other = (Rule) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public Rule clone() {
		Rule r = new Rule(getPosition(),getContext(),id,transformList(body,new Function<Expression,Expression>() {
			@Override
			public Expression apply(Expression x) {
				return x.substitute(NO_SUBTITUTIONS);
			}}),
		(FunctionInvocation)head.substitute(NO_SUBTITUTIONS));
		r.copyPropertiesTo(this);
		return r;
	}
	
	public Rule substitute(final Map<Expression,? extends Expression> substitutions) {
		Rule r = new Rule(getPosition(),getContext(),id,transformList(body,new Function<Expression,Expression>() {
			@Override
			public Expression apply(Expression x) {
				return x.substitute(substitutions);
			}}),
		(FunctionInvocation)head.substitute(substitutions));
		this.copyPropertiesTo(r);
		return r;
	}

	public void addToBody(Expression expression) {
		// clone returns unmodifiable list!
		List<Expression> newBody = new ArrayList<Expression>(body.size()+1);
		newBody.addAll(body);
		newBody.add(expression);
		body=newBody;
		
	}
	
	
	/**
	 * Clone the rule, and flatten NAF expressions in the body.
	 * If an expression is a unary expression using negation, and its part is a function invocation referencing a relationship,
	 * it will be replaced by just the function invocation with NAF set to true.
	 * @return
	 */	
	public Rule normaliseNAF () {
		Rule r = new Rule(getPosition(),getContext(),id,transformList(body,new Function<Expression,Expression>() {
			@Override
			public Expression apply(Expression x) {
				if (x instanceof UnaryExpression && ((UnaryExpression)x).getOperator()==UnOp.NOT) {
					Expression part = ((UnaryExpression)x).getPart();
					if (part instanceof FunctionInvocation && ((FunctionInvocation)part).isDefinedByRelationship()) {
						FunctionInvocation newPart = (FunctionInvocation)part.clone();
						newPart.setNaf(true);
						return newPart;
					}
				}
				return x;
			}}),
		(FunctionInvocation)head.substitute(NO_SUBTITUTIONS));
		r.copyPropertiesTo(this);
		return r;
	}
	
}

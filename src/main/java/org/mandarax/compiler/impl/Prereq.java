package org.mandarax.compiler.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import org.mandarax.compiler.CompilerException;
import org.mandarax.dsl.BinOp;
import org.mandarax.dsl.BinaryExpression;
import org.mandarax.dsl.Expression;
import org.mandarax.dsl.ExpressionPrinter;
import org.mandarax.dsl.FunctionDeclaration;
import org.mandarax.dsl.FunctionInvocation;
import org.mandarax.dsl.MemberAccess;
import org.mandarax.dsl.RelationshipDefinition;
import org.mandarax.dsl.Variable;
import org.mandarax.dsl.util.AnnotationKeys;
import org.mandarax.rt.Equals;
import static com.google.common.base.Preconditions.*;

/**
 * Annotation description of prerequisites. 
 * @author jens dietrich
 */
public class Prereq {
	private Expression expression = null;
	private Collection<Expression> newlyBoundVariables = new LinkedHashSet<Expression>(); 
	// a collection of bound variables
	private Collection<Expression> boundVariables = new LinkedHashSet<Expression>();
	
	
	// the previous prerequisite
	private Prereq previous = null;
	
	public Expression getExpression() {
		return expression;
	}
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	public Collection<Expression> getNewlyBoundVariables() {
		return newlyBoundVariables;
	}
	public void setNewlyBoundVariables(Collection<Expression> newlyBoundVariables) {
		this.newlyBoundVariables = newlyBoundVariables;
	}
	
	public boolean isBindsNewVariables() {
		return this.newlyBoundVariables.size()>0;
	}

	public boolean isDefinedByRelationship() {
		return expression instanceof FunctionInvocation && (((FunctionInvocation)expression).isDefinedByRelationship());
	}
	
	public boolean isFilter() {
		return !isDefinedByRelationship() || !isBindsNewVariables() || isNAF();
	}
	
	public boolean isNAF() {
		return isDefinedByRelationship() && ((FunctionInvocation)expression).isNaf();
	}
	
	public RelationshipDefinition getRel() {
		if (expression instanceof FunctionInvocation) {
			return (((FunctionInvocation)expression).getRelationship());
		}
		return null;
	}
	// look for the last relationship defined by previous prereqs
	public RelationshipDefinition getPreviousRel() {
		
		if (previous==null) return null;
		else if (previous.isDefinedByRelationship()) {
			return previous.getRel();
		}
		else {
			return previous.getPreviousRel();
		}
	}
	
	// get the query to be invoked for a function invocation that references a relationship definition
	public FunctionDeclaration getQuery() throws CompilerException {
		FunctionInvocation finv = (FunctionInvocation)getExpression();
		RelationshipDefinition rel = finv.getRelationship();
		boolean[] signature = new boolean[rel.getSlotDeclarations().size()];
		for (int i=0;i<signature.length;i++) {
			Expression term = finv.getParameters().get(i);
			signature[i]= term.isGround() || (isBound(term) && !isNewlyBound(term)); 
		}
		for (FunctionDeclaration query:rel.getQueries()) {
			if (Arrays.equals(signature,query.getSignature())) {
				return query;
			}
		}
		throw new CompilerException("Cannot find matching query for function invocation: " + finv);
	}
	public Collection<Expression> getBoundVariables() {
		return boundVariables;
	}
	public void setBoundVariables(Collection<Expression> boundVariables) {
		this.boundVariables = boundVariables;
	}
	
	private boolean isNewlyBound(Expression expression) {
		for (Variable var:expression.getVariables()) {
			if (!newlyBoundVariables.contains(var)) return false;
		}
		return true;
	}
	
	private boolean isBound(Expression expression) {
		for (Variable var:expression.getVariables()) {
			if (!boundVariables.contains(var)) return false;
		}
		return true;
	}

	public String printBoundParams(String scope,String arg) throws IOException,CompilerException {
		StringBuffer buf = new StringBuffer();
		buf.append(arg);
		for (String queryParameter:this.getQuery().getParameterNames()) {
			int slotPos = this.getRel().getSlotPosition(queryParameter);
			Expression expr = ((FunctionInvocation)expression).getParameters().get(slotPos);	
			//if (isBound(expr) && !isNewlyBound(expr) ) {
				buf.append(',');
				buf.append(print(expr,scope));
			//}
			
		}
		
		return buf.toString();
	}
	
	private String print(Expression expression,final String scope) throws IOException {
		
		StringBuffer b = new StringBuffer();
		ExpressionPrinter printer = getExpressionPrinter(scope,b);
		printer.print(expression);
		return b.toString();
	}
	// indicates whether this is the first prereq defined by a relation
	public boolean isFirstRelation() {
		for (Prereq pred:getPreds()) {
			if (pred.isDefinedByRelationship()) return false;
		}
		return this.isDefinedByRelationship();
	}
	
	public Prereq getPreviousRelPrereq() {
		for (Prereq prev:getPreds()) {
			if (prev.isDefinedByRelationship()) return prev;
		}
		return null;
	}
	
	private List<Prereq> getPreds() {
		List<Prereq> list = new ArrayList<Prereq>();
		if (previous!=null) {
			list.add(previous);
			list.addAll(previous.getPreds());
		}
		return list;
	}
	public Prereq getPrevious() {
		return previous;
	}
	public void setPrevious(Prereq previous) {
		this.previous = previous;
	}
	// get the name of the slot in the predicate that is referenced by a newly introduced variable
	public String getSlot(String variableName) throws CompilerException {
		checkState(expression instanceof FunctionInvocation,"Expression %s must be a function invocation",expression);
		FunctionInvocation fi = (FunctionInvocation)expression;
		checkState(fi.getRelationship()!=null,"Expression %s must be a function reference",fi);
		RelationshipDefinition rel = this.getRel();
		
		FunctionDeclaration query = getQuery();
		
		for (int i=0;i<fi.getParameters().size();i++) {
			Expression param = fi.getParameters().get(i);
			//checkState(param.isFlat(),"Only flat expressions are supported here"); 
			if (param instanceof Variable && ((Variable)param).getName().equals(variableName)) {
				return rel.getSlotDeclarations().get(i).getName();
			}
		}
		
		return null;
	
	}
	public boolean hasPreviousRelPrereq() {
		return this.getPreviousRelPrereq()!=null;
	}
	//print the expression, add scope to variable references
	public String printScoped(final String scope) throws IOException {
		StringBuffer b = new StringBuffer();
		ExpressionPrinter printer = getExpressionPrinter(scope,b);
		printer.print(this.expression);
		return b.toString();
	}
	
	private ExpressionPrinter getExpressionPrinter(final String scope,Appendable app) {
		ExpressionPrinter printer = new ExpressionPrinter(app) {
			@Override
			protected void doPrint(Variable var) throws IOException {
				if (var.getProperty(Scheduler.TYPE_NAME)!=null && ((Boolean)var.getProperty(Scheduler.TYPE_NAME)).booleanValue()) {
					out.append(var.getName());
				}
				else {
					out.append(scope);
					out.append('.');
					out.append(var.getName());
				}
			}
			// special printing for equals
			@Override
			protected void doPrint(BinaryExpression x) throws IOException {
				if (x.getOperator()==BinOp.EQ) {
					out.append(Equals.class.getName());
					out.append(".compare(");
					print(x.getLeft());
					out.append(',');
					print(x.getRight());
					out.append(')');
				}
				else {
					super.doPrint(x);
				}
			}
			// special printing for member access 
			@Override
			protected void doPrint(MemberAccess x) throws IOException {
				Member m = (Member) x.getProperty(AnnotationKeys.MEMBER);
				if (m==null) {
					DefaultCompiler.LOGGER.warn("Cannot find MEMBER annotation for " + x + " - will use default implementation to print code for member access");
					super.doPrint(x);
				}
				else {
					print(x.getObjectReference());
					out.append('.');
					if (m instanceof Method) {
						out.append(m.getName());
						appendListOfNodes(x.getParameters(),true,",");
					}
					else if (m instanceof Field) {
						out.append(m.getName());
					}
				}
			}
			
			// special printing for function invocation 
			@Override
			protected void doPrint(FunctionInvocation x) throws IOException {
				if (x.isDefinedByRelationship()) {
					super.doPrint(x);
				}
				Method m = (Method) x.getProperty(AnnotationKeys.MEMBER);
				if (m==null) {
					DefaultCompiler.LOGGER.debug("Cannot find MEMBER annotation for " + x + " - will use default implementation to print code for member access");
					super.doPrint(x);
				}
				else {
					out.append(m.getDeclaringClass().getName());
					out.append('.');
					out.append(m.getName());
					appendListOfNodes(x.getParameters(),true,",");
				}
					
			}
			
			
		};
		return printer;
	}
	
	@Override
	public String toString() {
		return this.expression==null?super.toString():this.expression.toString();
	}

}

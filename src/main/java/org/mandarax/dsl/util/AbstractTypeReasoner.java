package org.mandarax.dsl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.mandarax.dsl.*;

/**
 * Abstract superclass that can be used to implement TypeReasoners.
 * @author jens dietrich
 */
public abstract class AbstractTypeReasoner implements TypeReasoner {
	
	public static final Logger LOGGER = Logger.getLogger(AbstractTypeReasoner.class); 

	@Override
	public Class getType(Expression x,Resolver r,Collection<RelationshipDefinition> rels) throws TypeReasoningException {
		Class type = null;
		if (x instanceof BinaryExpression) type = doGetType((BinaryExpression)x,r,rels);
		else if (x instanceof BooleanLiteral) type = doGetType((BooleanLiteral)x,r,rels);
		else if (x instanceof CastExpression) type = doGetType((CastExpression)x,r,rels);
		else if (x instanceof ConditionalExpression) type = doGetType((ConditionalExpression)x,r,rels);
		else if (x instanceof InstanceOfExpression) type = doGetType((InstanceOfExpression)x,r,rels);
		else if (x instanceof IntLiteral) type = doGetType((IntLiteral)x,r,rels);
		else if (x instanceof DoubleLiteral) type = doGetType((DoubleLiteral)x,r,rels);
		else if (x instanceof MemberAccess) type = doGetType((MemberAccess)x,r,rels);
		else if (x instanceof StringLiteral) type = doGetType((StringLiteral)x,r,rels);
		else if (x instanceof UnaryExpression) type = doGetType((UnaryExpression)x,r,rels);
		else if (x instanceof Variable) type = doGetType((Variable)x,r,rels);
		else if (x instanceof FunctionInvocation) type = doGetType((FunctionInvocation)x,r,rels);
		else if (x instanceof NullValue) type = doGetType((NullValue)x,r,rels);
		else if (x instanceof Aggregation) type = doGetType((Aggregation)x,r,rels);
		else if (x instanceof ConstructorInvocation) type = doGetType((ConstructorInvocation)x,r,rels);
		else {
			LOGGER.warn("Unsupported expression type " + x.getClass().getName());
			throw new TypeReasoningException("Unsupported expression type " + x.getClass().getName());
		}
		
		// if (type==null) throw new TypeReasoningException("Cannot find or compute type for expression: " + x);
		x.setProperty(AnnotationKeys.TYPE,type);
		return type;
	}
	
	private void exception(Object... tokens) throws TypeReasoningException {
		StringBuffer b = new StringBuffer();
		for (Object s:tokens) b.append(s);
		LOGGER.error(b.toString());
		throw new TypeReasoningException(b.toString());
	}
	private void exception(Exception cause,Object... tokens) throws TypeReasoningException {
		StringBuffer b = new StringBuffer();
		for (Object s:tokens) b.append(s);
		LOGGER.error(b.toString(),cause);
		throw new TypeReasoningException(b.toString(),cause);
	}
	private void failed(Expression expression,String reason,Exception cause) throws TypeReasoningException {
		exception(cause,"Cannot calculate type for expression expression ",expression," defined in ",expression.getPosition(),(reason==null?"":(" - "+reason)));
	}
	private void failed(Expression expression,String reason) throws TypeReasoningException {
		exception("Cannot calculate type for expression expression ",expression," defined in ",expression.getPosition(),(reason==null?"":(" - "+reason)));
	}
	private void failed(Expression expression) throws TypeReasoningException {
		failed(expression,null);
	}
	private void mismatch(Expression expression,Class expectedType,Class foundType) throws TypeReasoningException {
		exception("Expression ",toString(expression)," defined in ",expression.getPosition()," is of type ",foundType," but expected type is ",expectedType);
	}
	private String toString(Expression x) {
		return x.toString();
	}

	protected Class doGetType (BinaryExpression expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException{
		Class type1 = getType(expression.getLeft(),resolver,rels);
		Class type2 = getType(expression.getRight(),resolver,rels);
		BinOp op = expression.getOperator();
		
		// boolean operators
		if (op==BinOp.AND || op==BinOp.OR) {
			if (type1!=Boolean.class) mismatch(expression.getLeft(),Boolean.class,type1);
			if (type2!=Boolean.class) mismatch(expression.getRight(),Boolean.class,type2);
			return Boolean.class;
		}
		// comparison 
		else if (op==BinOp.EQ || op==BinOp.NEQ) {
			return Boolean.class;
		}
		// operators can be used with comparables
		// it is sufficient if one of the two types is comparable
		else if (isComparison(op) && (Comparable.class.isAssignableFrom(type1) || Comparable.class.isAssignableFrom(type2))) {
			return Boolean.class;
		}
		// string concat, convert second argument to string using toString()
		else if (op==BinOp.PLUS && type1==String.class) {
			return String.class;
		} 
		// arithmetic
		else if (op==BinOp.PLUS || op==BinOp.TIMES || op==BinOp.DIV || op==BinOp.MOD) {
			if (isNumType(type1) && isNumType(type2)) {
				return getNumericCompositionType(type1,type2);
			}
		}
		
		failed(expression);
		return null; // will never reach this
		
	}
	protected Class doGetType (BooleanLiteral expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException{
		return Boolean.class;
	}
	protected Class doGetType (CastExpression expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException{
		try {
			return resolver.getType(expression.getContext(),expression.getTypeName());
		} catch (ResolverException e) {
			failed(expression,null,e);
		}
		return null; // will never reach this
	}
	protected Class doGetType (ConditionalExpression expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException{
		Class type1 = getType(expression.getCondition(),resolver,rels);
		if (type1!=Boolean.class) mismatch(expression.getCondition(),Boolean.class,type1);
		
		Class type2 = getType(expression.getIfTrue(),resolver,rels);
		Class type3 = getType(expression.getIfTrue(),resolver,rels);
		
		if (type2==type3) return type2;
		else if (type2.isAssignableFrom(type3)) return type2;
		else if (type3.isAssignableFrom(type2)) return type3;
		else if (isNumType(type1) && isNumType(type2)) {
			return getNumericCompositionType(type1,type2);
		}
		else {
			mismatch(expression.getIfFalse(),type2,type3);
			return null; // will never reach this
		}
		
	}
	protected Class doGetType (InstanceOfExpression expression,Resolver resolver,Collection<RelationshipDefinition> rels)  throws TypeReasoningException {
		return Boolean.class;
	}
	protected Class doGetType (IntLiteral expression,Resolver resolver,Collection<RelationshipDefinition> rels)  throws TypeReasoningException {
		return Integer.class;
	}
	protected Class doGetType (Aggregation aggr,Resolver resolver,Collection<RelationshipDefinition> rels)  throws TypeReasoningException {
		if (aggr.getFunction()==AggregationFunction.count) return Integer.TYPE;
		else if (aggr.getFunction()==AggregationFunction.avg) return Double.TYPE;
		else return getType(aggr.getVariable(),resolver,rels);
	}
	protected Class doGetType (DoubleLiteral expression,Resolver resolver,Collection<RelationshipDefinition> rels)  throws TypeReasoningException {
		return Double.class;
	}
	protected Class doGetType (MemberAccess expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException {
		String name = expression.getMember();
		Class type = getType(expression.getObjectReference(),resolver,rels);
		if (type==null) failed(expression,"Cannot compute type for " + expression.getObjectReference());
		List<String> paramTypes = new ArrayList<String>();
		for (Expression param:expression.getParameters()) {
			paramTypes.add(getType(param,resolver,rels).getName());
		}
		
		Member member = null;
		try {
			String[] paramNames = expression.isMethod()?paramTypes.toArray(new String[paramTypes.size()]):null;
			member = resolver.getMember(expression.getContext(),name,type.getName(),paramNames);
			expression.setProperty(AnnotationKeys.MEMBER, member);
		} catch (ResolverException e) {
			failed(expression,null,e);
		}
		if (member==null) {
			failed(expression,"cannot find member " + name + " in type " + type.getName());
		}
		else if (member instanceof Method) {
			return ((Method)member).getReturnType();
		} 
		else if (member instanceof Field) {
			return ((Field)member).getType();
		}
		failed(expression);
		return null; // will never reach this
		
	}
	
	protected Class doGetType (FunctionInvocation expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException {
		String name = expression.getFunction();
		
		// check whether function is a relationship reference
		// if so, this is a predicate and the return type is boolean
		// TODO: this will check only name and param number, not types
		for (RelationshipDefinition rel:rels) {			
			// expressions in rule heads directly reference relationships, not queries
			if (rel.getName().equals(name) && rel.getSlotDeclarations().size()==expression.getParameters().size()) {
				return Boolean.class;
			}
		}
		
		List<String> paramTypes = new ArrayList<String>();
		for (Expression param:expression.getParameters()) {
			paramTypes.add(getType(param,resolver,rels).getName());
		}
		String[] paramNames = paramTypes.toArray(new String[paramTypes.size()]);
		
		try {
			Method method = resolver.getFunction(expression.getContext(),name,paramNames);
			// enforce post condition
			if (!Modifier.isStatic(method.getModifiers())) {
				exception("Functions can only be defined by static methods, this method is not static: ",method);
			}
			else {
				expression.setProperty(AnnotationKeys.MEMBER, method);
				return method.getReturnType();
			}
		} catch (ResolverException e) {
			failed(expression,null,e);
		}
		
		failed(expression);
		return null; // will never reach this
		
	}
	
	protected Class doGetType (ConstructorInvocation expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException {
		String name = expression.getTypeName();
		try {
			return resolver.getType(expression.getContext(), name);
		} catch (ResolverException e) {
			failed(expression,null,e);
			return null;
		}
		
	}
	
	protected Class doGetType (StringLiteral expression,Resolver resolver,Collection<RelationshipDefinition> rels)  throws TypeReasoningException {
		return String.class;
	}
	
	protected Class doGetType (NullValue expression,Resolver resolver,Collection<RelationshipDefinition> rels)  throws TypeReasoningException {
		return Object.class;
	}
	
	protected abstract Class doGetType (Variable expression,Resolver resolver,Collection<RelationshipDefinition> rels)  throws TypeReasoningException ;
	
	protected Class doGetType (UnaryExpression expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException{
		UnOp op = expression.getOperator();
		Class type = getType(expression.getPart(),resolver,rels);
		if (op==UnOp.NOT) {
			if (type==Boolean.class || type==Boolean.TYPE) return Boolean.class;
			else mismatch(expression.getPart(),Boolean.class,type);
		}
		else if (op==UnOp.MINUS) {
			if (isNumType(type)) return type;
			else failed(expression.getPart(),"numeric type expected here");
		}
		else if (op==UnOp.COMPL) {
			if (isIntType(type)) {
				if (type==Long.class) return Long.class;
				else return Integer.class;
			}
			else failed(expression.getPart(),"integer type expected here");
		}
		failed(expression);
		return null; // will never reach this
	}
	
	private boolean isIntType(Class type) {
		return type==Integer.class || type==Long.class || type==Short.class || type==Character.class || type==Byte.class;
	} 
	private boolean isNumType(Class type) {
		return (type.isPrimitive() && type!=Boolean.class) || type==Integer.class || type==Long.class || type==Short.class || type==Character.class || type==Byte.class || type==Double.class || type==Float.class;
	} 
	
	private boolean isComparison(BinOp op) {
		return op==BinOp.LT || op==BinOp.GT || op==BinOp.LTE || op==BinOp.GTE;
	} 
	
	private Class getNumericCompositionType(Class type1,Class type2) {
		assert (isNumType(type1));
		assert (isNumType(type2));
		if (isIntType(type1) && isIntType(type2)) {
			if (type1==Long.class || type2==Long.class) {
				return Long.class;
			}
			else {
				return Integer.class;
			}
		}
		else if (type1==Float.class && type2==Float.class) {
			return Float.class;
		}
		else {
			return Double.class;
		}
	}

	
}

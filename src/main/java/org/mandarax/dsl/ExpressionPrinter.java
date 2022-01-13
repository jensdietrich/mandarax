
package org.mandarax.dsl;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.mandarax.dsl.Utils.nameForBinOp;
import static org.mandarax.dsl.Utils.nameForUnOp;
import java.io.IOException;
import java.util.List;



/**
 * Utility to generated code for expressions.
 * @author jens dietrich
 */
public class ExpressionPrinter {
	public ExpressionPrinter(Appendable b) {
		super();
		this.out = b;
	}

	protected Appendable out = null;
	
	public void print(Expression x) throws IOException {
		if (x instanceof BinaryExpression) doPrint((BinaryExpression)x);
		else if (x instanceof BooleanLiteral) doPrint((BooleanLiteral)x);
		else if (x instanceof CastExpression) doPrint((CastExpression)x);
		else if (x instanceof ConditionalExpression) doPrint((ConditionalExpression)x);
		else if (x instanceof InstanceOfExpression) doPrint((InstanceOfExpression)x);
		else if (x instanceof IntLiteral) doPrint((IntLiteral)x);
		else if (x instanceof MemberAccess) doPrint((MemberAccess)x);
		else if (x instanceof StringLiteral) doPrint((StringLiteral)x);
		else if (x instanceof UnaryExpression)  doPrint((UnaryExpression)x);
		else if (x instanceof Variable) doPrint((Variable)x);
		else if (x instanceof FunctionInvocation) doPrint((FunctionInvocation)x);
		else if (x instanceof ConstructorInvocation) doPrint((ConstructorInvocation)x);
		else if (x instanceof NullValue) doPrint((NullValue)x);
		else if (x instanceof Aggregation) doPrint((Aggregation)x);
		else throw new IOException("Unsupported expression type " + x.getClass().getName());
	}

	protected void doPrint(ConstructorInvocation x) throws IOException {
		out.append("new ");
		out.append(x.getTypeName());
		appendListOfNodes(x.getParameters(),true,",");
	}

	protected void doPrint(NullValue x) throws IOException {
		out.append("null");
	}

	protected void doPrint(FunctionInvocation x) throws IOException {
		out.append(x.getFunction());
		appendListOfNodes(x.getParameters(),true,",");
	}

	protected void doPrint(Variable x) throws IOException {
		out.append(x.getName());
	}

	protected void doPrint(UnaryExpression x) throws IOException {
		out.append(nameForUnOp(x.getOperator()));
		if (!x.getPart().isFlat()) out.append('(');
		print(x.getPart());
		if (!x.getPart().isFlat()) out.append(')');
	}

	protected void doPrint(StringLiteral x) throws IOException {
		out.append('\"');
		out.append(escapeJava(x.getValue()));
		out.append('\"');
	}

	protected void doPrint(MemberAccess x) throws IOException {
		print(x.getObjectReference());
		out.append('.');
		if (x.isMethod()) {
			out.append(x.getMember());
			this.appendListOfNodes(x.getParameters(),true,",");
		}
		else {
			// we need to support case when this is a field
			// now we assume that this is a getter
			// we also need to support special getter syntax such as is.. for boolean properties
			// THIS IS NOW DONE IN A SUBCLASS THAT HAS ACCESS TO THE ACTUAL (RESOLVED) MEMBER
			out.append("get");
			out.append(Character.toUpperCase(x.getMember().charAt(0)));
			out.append(x.getMember().substring(1));
			out.append("()");
		}
	}

	protected void doPrint(IntLiteral x) throws IOException {
		out.append(Integer.toString(x.getValue()));
	}

	protected void doPrint(InstanceOfExpression x) throws IOException {
		print(x.getObjectReference());
		out.append(" instanceof ");
		out.append(x.getTypeName());
	}
	
	protected void doPrint(Aggregation x) throws IOException {
		out.append(x.getFunction().name());
		out.append(" ");
		print(x.getVariable());
		out.append(" in ");
		print(x.getExpression());
	}

	protected void doPrint(ConditionalExpression x) throws IOException {
		out.append('(');
		print(x.getCondition());
		out.append(')');
		out.append('?');
		out.append('(');
		print(x.getIfTrue());
		out.append(')');
		out.append(':');
		out.append('(');
		print(x.getIfFalse());
		out.append(')');
	}

	protected void doPrint(CastExpression x) throws IOException {
		out.append('(');
		out.append(x.getTypeName());
		out.append(')');
		print(x.getObjectReference());
	}

	protected void doPrint(BooleanLiteral x) throws IOException {
		out.append(String.valueOf(x.getValue()));
	}

	protected void doPrint(BinaryExpression x) throws IOException {
		if (!x.getLeft().isFlat()) out.append('(');
		print(x.getLeft());
		if (!x.getLeft().isFlat()) out.append(')');
		
		out.append(nameForBinOp(x.getOperator()));
		
		if (!x.getRight().isFlat()) out.append('(');
		print(x.getRight());
		if (!x.getRight().isFlat()) out.append(')');
	}
	
	protected void appendListOfNodes(List<? extends Expression> list, boolean brackets,String sep) throws IOException {
		if (brackets) out.append('(');
		boolean f = true;
		for (Expression n:list) {
			if (f) f=false;
			else out.append(sep);
			print(n);
		}
		if (brackets) out.append(')');
	}

	public Appendable getOut() {
		return out;
	}
	
}

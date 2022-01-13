
package org.mandarax.dsl.util;

import java.io.PrintStream;

import org.mandarax.dsl.*;

/**
 * Utility to print the AST structure of an expression.
 * @author jens dietrich
 */

public class ExpressionStructurePrinter implements ASTVisitor {

	private PrintStream out = System.out;
	private int indent = 0; 
	private int maxIndent = 5;
	
	private void d() {indent = indent+1;}
	private void u() {indent = indent-1;}
	private void i() {
		for (int i=0;i<indent;i++) out.print('\t');
	}
	private void annotate(Expression x) {
		int ind = maxIndent-indent;
		if (ind<1) ind=1;
		for (int i=0;i<ind;i++) out.print('\t');
		out.print("// ");
		out.print(x.getClass().getName());
		out.print("  (");
		out.print(x.getPosition().getLine());
		out.print("  ");
		out.print(x.getPosition().getCharPosInLine());
		out.println(")");
	}
	
	public void println() {
		out.println();
	}
	@Override
	public boolean visit(BinaryExpression x) {
		i();
		out.print(x.getOperator());
		annotate(x);
		d();
		return true;
	}

	@Override
	public boolean visit(BooleanLiteral x) {
		i();
		out.print(x.getValue());
		annotate(x);
		return true;
	}


	@Override
	public boolean visit(CastExpression x) {
		i();
		out.print("cast to "+x.getTypeName());
		annotate(x);
		d();
		return true;
	}
	
	@Override
	public boolean visit(Aggregation x) {
		i();
		out.print(x.getFunction().name());
		annotate(x);
		d();
		return true;
	}

	@Override
	public boolean visit(ConditionalExpression x) {
		i();
		out.print("conditional");
		annotate(x);
		d();
		return true;
	}

	@Override
	public boolean visit(InstanceOfExpression x) {
		i();
		out.print("instance of "+x.getTypeName());
		annotate(x);
		d();
		return true;
	}

	@Override
	public boolean visit(IntLiteral x) {
		i();
		out.print(x.getValue());
		annotate(x);
		return true;
	}
	
	@Override
	public boolean visit(DoubleLiteral x) {
		i();
		out.print(x.getValue());
		annotate(x);
		return true;
	}
	
	@Override
	public boolean visit(NullValue x) {
		i();
		out.print("null");
		annotate(x);
		return true;
	}

	@Override
	public boolean visit(MemberAccess x) {
		i();
		out.print(x.getMember());
		annotate(x);
		d();
		return true;
	}
	
	@Override
	public boolean visit(FunctionInvocation x) {
		i();
		out.print(x.getFunction());
		annotate(x);
		d();
		return true;
	}
	
	@Override
	public boolean visit(ConstructorInvocation x) {
		i();
		out.print("new ");
		out.print(x.getTypeName());
		annotate(x);
		d();
		return true;
	}


	@Override
	public boolean visit(StringLiteral x) {
		i();
		out.print(x.getValue());
		annotate(x);
		return true;
	}

	@Override
	public boolean visit(UnaryExpression x) {
		i();
		out.print(x.getOperator());
		annotate(x);
		d();
		return true;
	}

	@Override
	public boolean visit(Variable x) {
		i();
		out.print(x.getName());
		annotate(x);
		return true;
	}

	@Override
	public void endVisit(BinaryExpression x) {
		u();
	}

	@Override
	public void endVisit(BooleanLiteral x) {}

	@Override
	public void endVisit(CastExpression x) {
		u();
	}

	@Override
	public void endVisit(ConditionalExpression x) {
		u();
	}

	@Override
	public void endVisit(InstanceOfExpression x) {
		u();
	}

	@Override
	public void endVisit(IntLiteral x) {}
	
	@Override
	public void endVisit(DoubleLiteral x) {}
	
	@Override
	public void endVisit(NullValue x) {}

	@Override
	public void endVisit(MemberAccess x) {
		u();
	}

	@Override
	public void endVisit(FunctionInvocation x) {
		u();
	}
	
	@Override
	public void endVisit(ConstructorInvocation x) {
		u();
	}

	@Override
	public void endVisit(StringLiteral x) {}

	@Override
	public void endVisit(UnaryExpression x) {
		u();
	}
	
	@Override
	public void endVisit(Aggregation x) {
		u();
	}

	@Override
	public void endVisit(Variable x) {}
	
	@Override
	public boolean visit(CompilationUnit x) {
		return false;
	}
	@Override
	public boolean visit(RelationshipDefinition x) {
		return false;
	}
	@Override
	public boolean visit(ExternalFacts x) {
		return false;
	}
	@Override
	public boolean visit(Annotation x) {
		return false;
	}
	@Override
	public boolean visit(FunctionDeclaration x) {
		return false;
	}
	@Override
	public boolean visit(ImportDeclaration x) {
		return false;
	}
	@Override
	public boolean visit(ObjectDeclaration x) {
		return false;
	}
	@Override
	public boolean visit(PackageDeclaration x) {
		return false;
	}
	@Override
	public boolean visit(VariableDeclaration x) {
		return false;
	}
	@Override
	public boolean visit(Rule x) {
		return false;
	}
	@Override
	public void endVisit(CompilationUnit x) {}
	@Override
	public void endVisit(RelationshipDefinition x) {}
	@Override
	public void endVisit(Annotation x) {}
	@Override
	public void endVisit(FunctionDeclaration x) {}
	@Override
	public void endVisit(ImportDeclaration x) {}
	@Override
	public void endVisit(ObjectDeclaration x) {}
	@Override
	public void endVisit(PackageDeclaration x) {}
	@Override
	public void endVisit(VariableDeclaration x) {}
	@Override
	public void endVisit(Rule x) {}
	@Override
	public void endVisit(ExternalFacts x) {}

}

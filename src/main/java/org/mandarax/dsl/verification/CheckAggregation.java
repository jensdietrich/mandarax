package org.mandarax.dsl.verification;

import java.util.ArrayList;
import java.util.Collection;
import org.mandarax.dsl.ASTVisitor;
import org.mandarax.dsl.AbstractASTVisitor;
import org.mandarax.dsl.Aggregation;
import org.mandarax.dsl.CompilationUnit;
import org.mandarax.dsl.Expression;
import org.mandarax.dsl.FunctionInvocation;
import org.mandarax.dsl.Variable;
import org.mandarax.dsl.VerificationErrorReporter;
import org.mandarax.dsl.VerificationException;
import org.mandarax.dsl.Verifier;

/**
 * Ensure that the aggregation used in aggregations occur in the relationship.
 * E.g. max v in MyRel(x,y) violates this - v is not a variable in the term MyRel(x,y). 
 * @author jens dietrich
 */
public class CheckAggregation implements Verifier {

	@Override
	public void verify(Collection<CompilationUnit> cus,VerificationErrorReporter errorHandler) throws VerificationException {
		for (CompilationUnit cu:cus) {
			final Collection<Aggregation> aggs = new ArrayList<Aggregation>();
			ASTVisitor visitor = new AbstractASTVisitor(){
				@Override
				public boolean visit(Aggregation agg) {
					aggs.add(agg);
					return super.visit(agg);
				}
				
			};
			cu.accept(visitor);
			for (Aggregation agg:aggs) {
				verify(cu,agg,errorHandler);
			}
		}
		
		
	}

	protected void verify(CompilationUnit cu,Aggregation agg,VerificationErrorReporter errorHandler) throws VerificationException {
		Variable v = agg.getVariable();
		FunctionInvocation fi = agg.getExpression();
		for (Expression param:fi.getParameters()) {
			if (param.equals(v)) return;
		}
		
		errorHandler.reportError(cu,"The variable ",v," in aggregation ",agg," at ",agg.getPosition()," does not occur as parameter in the function invocation");
	}

}

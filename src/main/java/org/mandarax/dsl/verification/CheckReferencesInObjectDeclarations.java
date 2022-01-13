package org.mandarax.dsl.verification;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mandarax.dsl.CompilationUnit;
import org.mandarax.dsl.ObjectDeclaration;
import org.mandarax.dsl.Variable;
import org.mandarax.dsl.VerificationErrorReporter;
import org.mandarax.dsl.VerificationException;
import org.mandarax.dsl.Verifier;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * Ensure that object references used in object declarations are already defined in other object declarations.
 * @author jens dietrich
 */
public class CheckReferencesInObjectDeclarations implements Verifier {

	@Override
	public void verify(Collection<CompilationUnit> cus,VerificationErrorReporter errorHandler) throws VerificationException {
		for (CompilationUnit cu:cus) {
			List<ObjectDeclaration> objDecls = cu.getObjectDeclarations();
			for (int i=0;i<objDecls.size();i++) {
				ObjectDeclaration objDecl = objDecls.get(i);
				Collection<String> varNames = Collections2.transform(objDecl.getDefaultValueDeclaration().getVariables(),new Function<Variable,String>(){
					@Override
					public String apply(Variable var) {
						return var.getName();
					}});
				// copy in mutable coll
				Set<String> varNames2 = new HashSet<String>(varNames);
				for (int j=0;j<i;j++) {
					varNames2.remove(objDecls.get(j).getName());
				}
				
				if (!varNames2.isEmpty()) {
					errorHandler.reportError(cu,"The object declaration at ",objDecl.getPosition()," references undefined object(s) " + Joiner.on(",").skipNulls().join(varNames2));
				}
				
			}
		}
	}

}

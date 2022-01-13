
package org.mandarax.dsl;

import java.util.Collection;
import org.mandarax.dsl.verification.*;
/**
 * Delegates verification to a chain of verifiers.
 * @author jens dietrich
 */
public class VerifyAll implements Verifier {
	
	final static Verifier[] ALL = {
		new CheckUniqueNamesInObjectDeclarations(),
		new CheckReferencesInObjectDeclarations(),
		new CheckUniqueIdsOfRules(),
		new CheckAggregation(),
		new CheckFreeVariablesInExternalFacts()
		//new CheckFreeVariablesInFacts()
	};
	
	public void verify(Collection<CompilationUnit> cus,VerificationErrorReporter errorHandler) throws VerificationException {
		for (Verifier verifier:ALL) {
			verifier.verify(cus, errorHandler);
		}
	}
}

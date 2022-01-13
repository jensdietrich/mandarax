
package org.mandarax.dsl;

import java.util.Collection;

/**
 * Tool to check the consistency of parsed scripts. The purpose of verifiers is to enforce semantic rules
 * that are not part of the grammar.
 * @author jens dietrich
 */
public interface Verifier {
	public void verify(Collection<CompilationUnit> cus,VerificationErrorReporter errorHandler) throws VerificationException ;
}

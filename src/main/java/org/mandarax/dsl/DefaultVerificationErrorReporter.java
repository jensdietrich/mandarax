
package org.mandarax.dsl;

import com.google.common.base.Joiner;

/**
 * Error reporter that throws an exception immediately whenever 
 * @author jens dietrich
 */
public class DefaultVerificationErrorReporter implements VerificationErrorReporter {

	@Override
	public void reportError(CompilationUnit cu, Object... message) throws VerificationException {
		throw new VerificationException("Error in compilation unit " + cu + " - " + Joiner.on("").join(message));
	}

}

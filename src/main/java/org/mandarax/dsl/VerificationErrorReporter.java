
package org.mandarax.dsl;
/**
 * Interface for error reporters. 
 * Possible strategies: 
 * <ol>
 * <li>Throw an exceptions.
 * <li>Log errors.
 * <li>Collect errors.
 * </ol>
 * @author jens dietrich
 *
 */
public interface VerificationErrorReporter {
	public void reportError(CompilationUnit cu,Object... message) throws VerificationException;
}

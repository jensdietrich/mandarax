
package org.mandarax.dsl;

/**
 * Semantic exceptions - throws if constraints that are not enforced by the ANTLR grammar are encountered.
 * This is a runtime exception to minimize interference with ANTLR.
 * @author jens dietrich
 */
@SuppressWarnings("serial")
public class InternalScriptException extends RuntimeException {

	public InternalScriptException() {}

	public InternalScriptException(String message) {
		super(message);
	}

	public InternalScriptException(Throwable cause) {
		super(cause);
	}

	public InternalScriptException(String message, Throwable cause) {
		super(message, cause);
	}

}

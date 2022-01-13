package org.mandarax.dsl.util;

/**
 * Exception thrown when reasoning about the types of expressions.
 * @author jens dietrich
 */

public class TypeReasoningException extends Exception {

	public TypeReasoningException() {
	}

	public TypeReasoningException(String message) {
		super(message);
	}

	public TypeReasoningException(Throwable cause) {
		super(cause);
	}

	public TypeReasoningException(String message, Throwable cause) {
		super(message, cause);
	}

}

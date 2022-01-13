package org.mandarax.dsl.util;

import org.mandarax.compiler.CompilerException;

/**
 * Exception thrown when resolving types.
 * @author jens dietrich
 */

public class ResolverException extends CompilerException {

	public ResolverException() {
	}

	public ResolverException(String message) {
		super(message);
	}

	public ResolverException(Throwable cause) {
		super(cause);
	}

	public ResolverException(String message, Throwable cause) {
		super(message, cause);
	}

}

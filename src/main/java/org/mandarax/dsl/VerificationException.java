
package org.mandarax.dsl;

import org.mandarax.MandaraxException;

/**
 * Exception used to report verification problems.
 * @author jens dietrich
 */
@SuppressWarnings("serial")
public class VerificationException extends MandaraxException {

	public VerificationException() {}

	public VerificationException(String message) {
		super(message);
	}

	public VerificationException(Throwable cause) {
		super(cause);
	}

	public VerificationException(String message, Throwable cause) {
		super(message, cause);
	}

}

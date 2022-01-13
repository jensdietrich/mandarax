
package org.mandarax;

/**
 * Top-level exception.
 * @author jens dietrich
 */
@SuppressWarnings("serial")
public class MandaraxException extends Exception {

	public MandaraxException() {}

	public MandaraxException(String message) {
		super(message);
	}

	public MandaraxException(Throwable cause) {
		super(cause);
	}

	public MandaraxException(String message, Throwable cause) {
		super(message, cause);
	}

}

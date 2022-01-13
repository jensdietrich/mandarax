
package org.mandarax.rt;

/**
 * Exceptions that is thrown to signal that the derivation has been cancelled.
 * @author jens dietrich
 */

public class DerivationCancelledException extends DerivationException {

	public DerivationCancelledException() {
		super();
	}

	public DerivationCancelledException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DerivationCancelledException(String arg0) {
		super(arg0);
	}

	public DerivationCancelledException(Throwable arg0) {
		super(arg0);
	}

}

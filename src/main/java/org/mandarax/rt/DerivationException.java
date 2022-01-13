
package org.mandarax.rt;

/**
 * Exceptions that can be thrown when next(), hasNext() or close() are invoked.
 * This are runtime exceptions. The main reason for this is compatibility with java.util.Iterator. 
 * I.e., users can work with the well-known iterator interface. 
 * @author jens dietrich
 */

public class DerivationException extends RuntimeException {

	public DerivationException() {
		super();
	}

	public DerivationException(String m, Throwable t) {
		super(m, t);
	}

	public DerivationException(String m) {
		super(m);
	}

	public DerivationException(Throwable t) {
		super(t);
	}

}

package org.mandarax.rt;

/**
 * Exception thrown when iterating over an iterator that has been closed.
 * @author jens dietrich
 */
public class IteratorClosedException extends DerivationException {

	public IteratorClosedException() {
		this("This iterator has been closed");
	}

	public IteratorClosedException(String m, Throwable t) {
		super(m, t);
	}

	public IteratorClosedException(String m) {
		super(m);
	}

	public IteratorClosedException(Throwable t) {
		super(t);
	}

}

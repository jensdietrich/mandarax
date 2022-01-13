
package org.mandarax.rt;

import java.util.List;
/**
 * Result set. Wraps a resource iterator. Has an additional reference to the derivation log.
 * @author jens dietrich
 */
public class ResultSet<T> implements ResourceIterator<T>{

	private DerivationController derivationController = null;
	private ResourceIterator<T> delegate = null;
	
	public ResultSet( ResourceIterator<T> delegate,DerivationController derivationController) {
		super();
		this.derivationController = derivationController;
		this.delegate = delegate;
	}

	/**
	 * Close the iterator.
	 */
	public void close() {
		this.delegate.close();
	}

	public List<DerivationLogEntry> getDerivationLog() {
		return derivationController.getLog();
	}

	public DerivationController getDerivationController() {
		return derivationController;
	}
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	public T next() {
		return this.delegate.next();
	}

	public void remove() {
		this.delegate.remove();
	}
	
	/**
	 * Cancel the derivation.
	 */
	public void cancel() {
		this.derivationController.cancel();
	}
	/**
	 * Whether the derivation has been cancelled.
	 * @return the cancelled status
	 */
	public boolean isCancelled() {
		return this.derivationController.isCancelled();
	}


}


package org.mandarax.rt;


/**
 * Abstract iterator. remove is not supported by subclasses.
 * @author jens dietrich
 */
public abstract class AbstractIterator<T> implements ResourceIterator<T>{

	
	/**
	 * Remove the current element.
	 * Not supported.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}


package org.mandarax.rt;

/**
 * Empty iterator. Uses the singleton pattern.
 * @author jens dietrich
 * @param <T> the type of the iterated element
 */

public class EmptyIterator extends AbstractIterator {

	/**
	 * Return the default instance.
	 * @return the instance
	 */
	public static ResourceIterator DEFAULT = new EmptyIterator() ; 
	
	private EmptyIterator() {
		super();
	}

	public boolean hasNext() {
		return false;
	}

	public Object next() {
		throw new RuntimeException("this is an empty iterator");
	}

	public void close() {
		// nothing to do here
	}



}

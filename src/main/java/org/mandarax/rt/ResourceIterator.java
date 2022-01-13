
package org.mandarax.rt;

import java.util.Iterator;
/**
 * Resource iterator.
 * This is an iterator that can be closed. The close operation releases resources, e.g.
 * closes database connections.
 * Note that close(), next() and hasNext() may throw unchecked exceptions IteratorClosedException.
 * @see DerivationException
 * @author jens dietrich
 */
public interface ResourceIterator<T> extends Iterator<T>{


	/**
	 * Close the iterator.
	 */
	public void close();



}

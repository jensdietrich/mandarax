package org.mandarax.rt;

import java.util.Iterator;


/**
 * Resource iterator wrapper for a normal iterator.
 * Close does nothing.
 * @author jens dietrich
 */
public class NonClosableResourceIterator<T> extends AbstractIterator<T>{
	
	private Iterator<T> delegate = null;

	public NonClosableResourceIterator(Iterator<T> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void close() {}

	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public T next() {
		return delegate.next();
	}
	
	
}

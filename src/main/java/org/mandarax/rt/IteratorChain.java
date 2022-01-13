package org.mandarax.rt;

/**
 * Chain of iterators. Alternative implementation that chains iterators and objects.
 * This is more effective than wrapping objects as singleton iterators.
 * @author jens dietrich
 * @param <T> the type of the iterated elements
 */

public abstract class IteratorChain<T> extends AbstractIterator<T>{
	private int cursor = -1;
	private ResourceIterator<T> delegate = null;
	private ResourceIterator<T>[] parts = null;
	private boolean closed = false;

	
	@SuppressWarnings("unchecked")
	public IteratorChain(int size) {
		super();
		this.parts = new ResourceIterator[size];
	}
	private void moveCursor() {
		cursor = cursor+1;
		if (cursor<parts.length){
			ResourceIterator<T> iter = this.getNextIterator(cursor);
			if (iter==null) {
				delegate = EmptyIterator.DEFAULT;
			}
			else {
				parts[cursor]=iter;
				delegate = iter;
			}
		}
		else {
			delegate = null;
		}
	}
	@Override 
	public boolean hasNext() {
		if (closed) return false;
		
		if (cursor==parts.length) return false;
		else if (cursor==-1) {
			moveCursor();
			return hasNext();
		}
		else if (delegate.hasNext()) {
			return true;
		}
		else {
			moveCursor();
			return hasNext();
		}
	}
	public T next() {
		if (closed) throw new IteratorClosedException();
		
		if (hasNext()) {
			return delegate.next();
		}
		else 
			return null;
	}
	/**
	 * Get the iterator at the given position.
	 * @param pos the index
	 * @return an iterator
	 */
	public abstract ResourceIterator<T> getNextIterator(int pos);

	/**
	 * Close the iterator.
	 */
	@Override
	public void close() {
		for (ResourceIterator iter:this.parts) {
			if (iter!=null)
				iter.close();
		}
		closed = true;
	}

}



package org.mandarax.rt;

import com.google.common.base.Predicate;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Simple filtered iterator.
 * @author jens dietrich
 */
public class FilteredIterator<T> extends com.google.common.collect.AbstractIterator<T> implements ResourceIterator<T> {
	private ResourceIterator<T> unfiltered = null;
	private Predicate<? super T> predicate = null;
	private boolean closed = false;

	public FilteredIterator(ResourceIterator<T> unfiltered, Predicate<? super T> predicate) {
		super();
		this.unfiltered = unfiltered;
		this.predicate = predicate;
	}


	@Override
	protected T computeNext() {
		if (closed) throw new IteratorClosedException();
		
		checkNotNull(unfiltered);
		checkNotNull(predicate);
		while (unfiltered.hasNext()) {
			T element = unfiltered.next();
			if (predicate.apply(element)) {
				return element;
			}
		}
		return endOfData();
	}

	@Override
	public synchronized void close() {
		closed = true;
		unfiltered.close();
	}

	
}

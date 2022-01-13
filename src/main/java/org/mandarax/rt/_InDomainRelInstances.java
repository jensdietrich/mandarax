
package org.mandarax.rt;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

/**
 * Built-in queries for inDomain relationship.
 * @author jens dietrich
 */
public class _InDomainRelInstances {
	public ResultSet<_InDomainRel> getElements (Iterable container) {
		DerivationController _derivation = new DefaultDerivationController();
		return new ResultSet<_InDomainRel>(getElements ( _derivation ,  container ),_derivation);
	} 
	
	public static ResourceIterator<_InDomainRel> getElements ( final DerivationController _derivation ,  final Iterable container  ) {
		final int _derivationlevel = _derivation.size();
		final Iterator iterator = container.iterator();
		ResourceIterator<_InDomainRel> r = new ResourceIterator<_InDomainRel>() {
			int count = 0;
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public _InDomainRel next() {
				_derivation.pop(_derivationlevel);
				_derivation.log("In.genfact"+(count++), DerivationController.INDOMAIN, new Properties());
				_InDomainRel rel = new _InDomainRel();
				rel.container = container;
				rel.element = iterator.next();
				return rel;
			}

			@Override
			public void remove() {
				iterator.remove();
			}

			@Override
			public void close() {
				if (iterator instanceof Closeable) {
					try {
						((Closeable) iterator).close();
					}
					catch (IOException x) {
						errorOnClose(container);
					}
				}
			}
		};
		return r;
	} 
	
	public ResultSet<_InDomainRel> contains (Iterable container, final Object element) {
		DerivationController _derivation = new DefaultDerivationController();
		return new ResultSet<_InDomainRel>(contains ( _derivation ,  container, element ),_derivation);
	} 
	
	
	public static ResourceIterator<_InDomainRel> contains ( final DerivationController _derivation ,  final Iterable container , final Object element ) {
		final int _derivationlevel = _derivation.size();
		final Iterator iterator = Iterators.filter(container.iterator(),new Predicate() {
			@Override
			public boolean apply(Object e) {
				return element.equals(e);
			}});
		
		ResourceIterator<_InDomainRel> r = new ResourceIterator<_InDomainRel>() {
			int count = 0;
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public _InDomainRel next() {
				_derivation.pop(_derivationlevel);
				_derivation.log("In.genfact"+(count++), DerivationController.INDOMAIN, new Properties());
				_InDomainRel rel = new _InDomainRel();
				rel.container = container;
				rel.element = iterator.next();
				return rel;
			}

			@Override
			public void remove() {
				iterator.remove();
			}

			@Override
			public void close() {
				if (iterator instanceof Closeable) {
					try {
						((Closeable) iterator).close();
					}
					catch (IOException x) {
						errorOnClose(container);
					}
				}
			}
			
		};
		return r;
	}
	
	private static void errorOnClose(Iterable container) {
		// TODO create and use Mandarax runtime exception
		throw new RuntimeException("Error closing InRel iterator for data source " + container);		
	}
	
}


package org.mandarax.rt;

/**
 * Interface for derivation listener.
 * @author jens dietrich
 */

public interface DerivationListener {
	/**
	 * Notify the listener.  
	 * @param ruleRef
	 * @param derivationDepth
	 */
	public void step(String ruleRef,int derivationDepth) ;
}

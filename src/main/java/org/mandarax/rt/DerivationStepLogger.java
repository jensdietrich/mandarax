
package org.mandarax.rt;

import java.util.Properties;

/**
 * Interface used for logging derivation steps. 
 * @author jens dietrich
 */
public interface DerivationStepLogger {
	/**
	 * Prints a single derivation step event to a string buffer.
	 * @param ruleRef - the id of the rule, fact ... invoked
	 * @param kind - the kind of artefact (rule, fact etc) - one of the constants defined in DerivationController
	 * @param annotations - the meta data attached to the rule, facts ..
	 * @param derivationDepth - the current depth of the derivation
	 */
	public void print(String ruleRef,int kind,Properties annotations,int derivationDepth);
}

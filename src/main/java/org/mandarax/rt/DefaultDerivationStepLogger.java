
package org.mandarax.rt;

import java.util.Properties;
import org.apache.log4j.*;


/**
 * Default logger. 
 * @author jens dietrich
 */
public class DefaultDerivationStepLogger implements DerivationStepLogger{

	
	private Logger LOGGER = Logger.getLogger(DerivationController.class.getName());
	
	/**
	 * Prints a single derivation step event to a string buffer.
	 * @param ruleRef - the id of the rule, fact ... invoked
	 * @param kind - the kind of artefact (rule, fact etc) - one of the constants defined in DerivationController
	 * @param annotations - the meta data attached to the rule, facts ..
	 * @param derivationDepth - the current depth of the derivation
	 */
	@Override
	public void print(String ruleRef,int kind,Properties annotations,int derivationDepth) {
		StringBuffer b = new StringBuffer();
		for (int i=0;i<derivationDepth;i++) b.append("> "); 
		b.append(ruleRef);
		LOGGER.debug(b.toString());
	}
}

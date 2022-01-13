
package org.mandarax.rt;

/**
 * Factory that creates the default derivation step logger using log4j.
 * To enable logging in an application, execute <code>new EnableDefaultLogging().install()</code>
 * before the application starts.
 * @author jens dietrich
 */
public class EnableDefaultLogging extends DerivationStepLoggerFactory{
	
	/**
	 * Create a logger. Returning null is permitted - this means no logging will take place.
	 * @return
	 */
	public DerivationStepLogger createLogger() {
		return new DefaultDerivationStepLogger();
	}

}

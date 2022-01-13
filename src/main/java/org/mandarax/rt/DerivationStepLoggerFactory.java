
package org.mandarax.rt;

/**
 * Factory used to create loggers. 
 * The client will use the instance stored in defaultInstance. To modify this, create a subclass and install it with
 * install when the program starts.
 * @author jens dietrich
 */
public class DerivationStepLoggerFactory {

	
	static DerivationStepLoggerFactory defaultInstance = new DerivationStepLoggerFactory();
	
	// uncommenting this will switch on logging
//	static {
//		new EnableDefaultLogging().install();
//	}
	
	/**
	 * Create a logger. Returning null is permitted - this means no logging will take place.
	 * @return
	 */
	public DerivationStepLogger createLogger() {
		return null;
	}
	
	public void install() {
		defaultInstance = this;
	}
}

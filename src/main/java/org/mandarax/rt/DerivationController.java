
package org.mandarax.rt;

import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

/**
 * Utility class to control the derivation process.
 * Responsibilities:
 * <ol>
 * <li>record the derivation tree, and return it to the application</li>
 * <li>interrupt derivations</li>
 * <li>optional: progress notification</li>
 * <li>optional: loop checking</li>
 * <li>optional: stop derivation after a certain number of steps</li>
 * </ol>
 * Used to record derivation trees, and can be used to cancel the inference process.
 * Recorded are string ids of the rules, facts etc, applications can use this strings to 
 * access artefacts in the knowledge base.
 * @author jens dietrich
 * @param <T> the type of the iterated elements
 */

public interface DerivationController  {
	// constant signaling that no parameter has been provided
	public static final Object NIL = new Object() {
		public String toString() {
			return "?";
		}
	};
	// constants to be used as parameters in log
	public static final int ANY = 0;
	public static final int RULE = 1;
	public static final int FACT = 2;
	public static final int EXTERNAL = 3;
	public static final int INDOMAIN = 4;
	
	/**
	 * Log the use of a clause set
	 * @param ruleRef a string referencing the knowledge element (id or similar)
	 * @param in kind what kind of knowledge this is (one of the constants RULE, FACT etc)
	 * @param the annotations used
	 */
	public void log(String ruleRef,int kind,Properties annotations) ;
	/**
	 * Get (a copy of) the derivation log. 
	 * May throw a runtime exception (e.g., if the derivation has been cancelled). 
	 * @return a list
	 */
	public List<DerivationLogEntry> getLog() ;
	
	/**
	 * Print the log to a print stream.
	 * @param out a print stream 
	 */
	public void printLog(PrintStream out) ;
	/**
	 * Print the log to System.out.
	 */
	public void printLog() ;
	/**
	 * Get the derivation level.
	 * @return
	 */
	public int size() ;
	/**
	 * Increase the derivation level.
	 * @return this
	 */
	public DerivationController push() ;
	/**
	 * Reset the derivation level.
	 * @param value
	 * @return this
	 */
	public DerivationController pop(int value);
	/**
	 * Cancel the derivation.
	 */
	public void cancel() ;
	/**
	 * Whether the derivation has been canceled. If the derivation is canceled, the next call to log should
	 * trigger a DerivationCancelledException. 
	 * This can be used by applications to stop long running derivations.
	 * @return the canceled status
	 */
	public boolean isCancelled() ;

}

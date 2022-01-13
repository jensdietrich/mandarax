
package org.mandarax.rt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Data structure to store the reference to elements in the knowledge base.
 * Used to record derivation trees, and can be used to cancel the inference process.
 * Recorded are string ids of the rules, facts etc, applications can use this strings to 
 * access artefacts in the knowledge base.
 * @author jens dietrich
 */

public class DefaultDerivationController  implements DerivationController {
	
	private List<String> ids = new ArrayList<String>();
	private List<Integer> types = new ArrayList<Integer>();
	private List<Properties> annotations = new ArrayList<Properties>();
	private int depth = 0;
	private boolean cancelled = false;
	private DerivationListener derivationListener = null;
	private DerivationStepLogger logger = DerivationStepLoggerFactory.defaultInstance.createLogger();

	
	private static final Properties NO_ANNOTATIONS = new Properties();
	
	/**
	 * Log the use of a clause set
	 * this implementation does not record the parameters
	 */
	public synchronized void log(String ruleRef,int kind,Properties annotations) {
		if (cancelled) 
			throw new DerivationCancelledException();
		
		this.ids.add(depth,ruleRef);	
		this.types.add(depth,kind);
		this.annotations.add(depth,annotations==null?NO_ANNOTATIONS:annotations);
		
		if (logger!=null) {
			logger.print(ruleRef, kind, annotations, depth);
		}
		
		if (derivationListener!=null) {
			derivationListener.step(ruleRef, depth);
		}
	}
	/**
	 * Get a copy of the derivation log. 
	 * @return a list
	 */
	public synchronized List<DerivationLogEntry> getLog() {
		List<DerivationLogEntry> list = new ArrayList<DerivationLogEntry>();
		for (int i=0;i<=depth;i++) {
			String s = this.ids.get(i);
			if (s!=null) {
				list.add(new DerivationLogEntry(this.ids.get(i),this.types.get(i),this.annotations.get(i)));
			}
		}
			
		return list;
	}
	
	/**
	 * Print the log to a print stream.
	 * @param out a print stream 
	 */
	public synchronized void printLog(PrintStream out) {
		for (int i=0;i<=depth;i++) {
			String s = this.ids.get(i);
			if (s!=null) {
				out.print(i+1);
				out.print(". ");
				out.println(s);	
			}
		}
	}
	/**
	 * Print the log to System.out.
	 */
	public synchronized void printLog() {
		printLog(System.out);
	}
	/**
	 * Get the derivation level.
	 * @return
	 */
	public synchronized int size() {
		return depth;
	}
	/**
	 * Increase the derivation level.
	 * @return this
	 */
	public synchronized DefaultDerivationController push() {
		this.depth = depth+1;
		return this;
	}
	/**
	 * Reset the derivation level.
	 * @param value
	 * @return this
	 */
	public synchronized DefaultDerivationController pop(int value) {
		//System.out.println("Set depth=" + value);
		assert value<=depth;
		this.depth = value;
		return this;
	}
	/**
	 * Cancel the derivation.
	 */
	public synchronized void cancel() {
		this.cancelled = true;
	}
	/**
	 * Whether the derivation has been cancelled.
	 * @return the cancelled status
	 */
	public synchronized boolean isCancelled() {
		return this.cancelled;
	}

	public synchronized DerivationListener getDerivationListener() {
		return derivationListener;
	}
	public synchronized void setDerivationListener(DerivationListener derivationListener) {
		this.derivationListener = derivationListener;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int i=0;i<depth;i++) {
			if (b.length()>0) b.append(',');
			b.append(ids.get(i)); 
		}
		return b.toString();
	}
}

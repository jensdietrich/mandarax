
package org.mandarax.examples.userv.domain;

/**
 * Bean class that is part of the example domain model.
 * http://www.businessrulesforum.com/2005_Product_Derby.pdf 
 * @author jens dietrich
 */

public class Policy {
	private boolean includesUninsuredMotoristCoverage = false;
	private boolean includesMedicalCoverage = false;
	public boolean includesUninsuredMotoristCoverage() {
		return includesUninsuredMotoristCoverage;
	}
	public void setIncludesUninsuredMotoristCoverage(
			boolean includesUninsuredMotoristCoverage) {
		this.includesUninsuredMotoristCoverage = includesUninsuredMotoristCoverage;
	}
	public boolean includesMedicalCoverage() {
		return includesMedicalCoverage;
	}
	public void setIncludesMedicalCoverage(boolean includesMedicalCoverage) {
		this.includesMedicalCoverage = includesMedicalCoverage;
	}
	
}

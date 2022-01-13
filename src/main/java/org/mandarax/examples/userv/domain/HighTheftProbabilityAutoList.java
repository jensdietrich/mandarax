
package org.mandarax.examples.userv.domain;

import java.util.Collection;
import java.util.HashSet;

/**
 * List of domain objects used in the example.
 * http://www.businessrulesforum.com/2005_Product_Derby.pdf 
 * @author jens dietrich
 */

public class HighTheftProbabilityAutoList {
	
	private static Collection<String> list = null;
	
	public static Collection<String> getList() {
		if (list==null) {
			list = new HashSet<String>();
			list.add("Mini");	
			list.add("VW Beetle");
			list.add("VW Phaeton");
			list.add("Audi A3");
			list.add("Mercedes Benz A class");
		}
		return list;
	}

}

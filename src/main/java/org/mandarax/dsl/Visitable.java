
package org.mandarax.dsl;

/**
 * Interface for objects that accept visitors.
 * @author jens dietrich
 */

public interface Visitable {
	public void accept(ASTVisitor visitor);
}

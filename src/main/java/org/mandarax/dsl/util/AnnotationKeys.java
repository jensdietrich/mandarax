
package org.mandarax.dsl.util;
/**
 * Keys that are used by the resolver and type reasoner to add additional information to AST nodes.
 * This keys can be used to retrieve those properties using the getProperty method.
 * @author jens dietrich
 */
public enum AnnotationKeys {
	MEMBER, // the reflect object (field or method) associated with a member access
	TYPE // the expression type (java.lang.Class)
}

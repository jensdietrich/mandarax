package org.mandarax.dsl.util;

import java.util.Collection;
import org.mandarax.dsl.Expression;
import org.mandarax.dsl.RelationshipDefinition;

/**
 * Utility to associate type information with expressions.
 * @author jens dietrich
 */

public interface TypeReasoner {
	Class getType(Expression expression,Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException;
}

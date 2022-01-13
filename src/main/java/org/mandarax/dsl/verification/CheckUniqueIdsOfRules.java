package org.mandarax.dsl.verification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.mandarax.dsl.CompilationUnit;
import org.mandarax.dsl.RelationshipDefinition;
import org.mandarax.dsl.RelationshipDefinitionPart;
import org.mandarax.dsl.Rule;
import org.mandarax.dsl.VerificationErrorReporter;
import org.mandarax.dsl.VerificationException;
import org.mandarax.dsl.Verifier;

/**
 * Ensure that rule ids are unique.
 * @author jens dietrich
 */
public class CheckUniqueIdsOfRules implements Verifier {

	@Override
	public void verify(Collection<CompilationUnit> cus,VerificationErrorReporter errorHandler) throws VerificationException {
		for (CompilationUnit cu:cus) {
			for (RelationshipDefinition rel:cu.getRelationshipDefinitions()) {
				Set<String> ids = new HashSet<String>();
				for (RelationshipDefinitionPart defPart:rel.getDefinitionParts()) {
					String id = defPart.getId();
					if (ids.contains(id)) {
						errorHandler.reportError(cu,"The id used by rule ",defPart," at ", defPart.getPosition()," has already been used within this relationship definition");
					}
					else {
						ids.add(id);
					}
				}
			}
		}
		
	}

}

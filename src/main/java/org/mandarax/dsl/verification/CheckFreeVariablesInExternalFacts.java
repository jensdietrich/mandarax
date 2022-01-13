package org.mandarax.dsl.verification;

import java.util.Collection;
import org.mandarax.dsl.CompilationUnit;
import org.mandarax.dsl.ExternalFacts;
import org.mandarax.dsl.ObjectDeclaration;
import org.mandarax.dsl.RelationshipDefinition;
import org.mandarax.dsl.RelationshipDefinitionPart;
import org.mandarax.dsl.Variable;
import org.mandarax.dsl.VerificationErrorReporter;
import org.mandarax.dsl.VerificationException;
import org.mandarax.dsl.Verifier;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Ensure that external fact sets have only ground terms.
 * @author jens dietrich
 */
public class CheckFreeVariablesInExternalFacts implements Verifier {

	@Override
	public void verify(Collection<CompilationUnit> cus,VerificationErrorReporter errorHandler) throws VerificationException {
		for (CompilationUnit cu:cus) {
			// collect declared objects
			Collection<String> objNames = Collections2.transform(cu.getObjectDeclarations(),new Function<ObjectDeclaration,String>(){
				@Override
				public String apply(ObjectDeclaration obj) {
					return obj.getName();
				}});
			for (RelationshipDefinition rel:cu.getRelationshipDefinitions()) {
				for (RelationshipDefinitionPart part:rel.getDefinitionParts()) {
					if (part instanceof ExternalFacts) {
						ExternalFacts facts = (ExternalFacts)part;
						for (Variable var:facts.getIterable().getVariables()) {
							if (!objNames.contains(var.getName())) {
								errorHandler.reportError(cu,"The term ",var," at ", facts.getPosition()," in the external fact set ",facts.getId()," cannot be a free variable");
							}
						}
					}

				}
			}
		}
		
	}

}

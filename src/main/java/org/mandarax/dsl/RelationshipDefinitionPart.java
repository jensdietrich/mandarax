
package org.mandarax.dsl;

/**
 * Abstract superclass for artefacts used to define the semantics of relationships.
 * @author jens dietrich
 */
public abstract class RelationshipDefinitionPart extends AnnotatableNode implements Cloneable {
	
	protected String id = null;
	
	public String getId() {
		return id;
	}

	public RelationshipDefinitionPart(Position position, Context context,String id) {
		super(position, context);
		this.id = id;
	}
	
}

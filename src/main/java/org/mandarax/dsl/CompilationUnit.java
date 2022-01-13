
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a top-level compilation unit.
 * @author jens dietrich
 */
public class CompilationUnit extends ASTNode {

	private List<RelationshipDefinition> relationshipDefinitions = new ArrayList<RelationshipDefinition>() ;
	private List<ObjectDeclaration> objectDeclarations = new ArrayList<ObjectDeclaration>() ;
	// this is the name of a file or the URL from where this cu has been read
	private String source = "unknown source"; 
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public CompilationUnit(Position position, Context context) {
		super(position, context);
	}

	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			for (ObjectDeclaration f:this.objectDeclarations) f.accept(visitor);
			for (RelationshipDefinition v:this.relationshipDefinitions) v.accept(visitor);
		}
		visitor.endVisit(this);
	}

	public List<RelationshipDefinition> getRelationshipDefinitions() {
		return relationshipDefinitions;
	}
	
	public void add(RelationshipDefinition relDef) {
		this.relationshipDefinitions.add(relDef);
	}
	
	public List<ObjectDeclaration> getObjectDeclarations() {
		return objectDeclarations;
	}
	
	public void add(ObjectDeclaration objDecl) {
		this.objectDeclarations.add(objDecl);
	}
	
	// deep access methods
	public PackageDeclaration getPackageDeclaration() {
		return this.getContext().getPackageDeclaration(); 
	}
	
	public List<ImportDeclaration> getImportDeclarations() {
		return this.getContext().getImportDeclarations(); 
	}
	
	public List<ImportDeclaration> getStaticImportDeclarations() {
		return this.getContext().getStaticImportDeclarations(); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((objectDeclarations == null) ? 0 : objectDeclarations
						.hashCode());
		result = prime
				* result
				+ ((relationshipDefinitions == null) ? 0
						: relationshipDefinitions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompilationUnit other = (CompilationUnit) obj;
		if (objectDeclarations == null) {
			if (other.objectDeclarations != null)
				return false;
		} else if (!objectDeclarations.equals(other.objectDeclarations))
			return false;
		if (relationshipDefinitions == null) {
			if (other.relationshipDefinitions != null)
				return false;
		} else if (!relationshipDefinitions
				.equals(other.relationshipDefinitions))
			return false;
		return true;
	}
	
	public String toString() {
		return "compilation unit("+source+")";
	}
	

}

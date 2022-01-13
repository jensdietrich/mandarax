
package org.mandarax.dsl;

/**
 * Package declaration.
 * @author jens dietrich
 */
public class PackageDeclaration extends ASTNode {
	
	private String name = null;

	public PackageDeclaration(Position position, Context context,String name) {
		super(position, context);
		this.name = name;
		
		context.setPackageDeclaration(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}


	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("package ");
		b.append(name);
		b.append(';');
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PackageDeclaration other = (PackageDeclaration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}

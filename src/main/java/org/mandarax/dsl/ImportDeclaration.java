
package org.mandarax.dsl;


/**
 * Import declaration.
 * @author jens dietrich
 */
public class ImportDeclaration extends ASTNode {
	
	private boolean staticImport = false;
	private boolean usesWildcard = false;
	private String name = null;

	public ImportDeclaration(Position position, Context context,String name,boolean isStatic,boolean usesWildcard) {
		super(position, context);
		this.name = name;
		this.staticImport = isStatic;
		this.usesWildcard = usesWildcard;
		context.add(this);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public boolean isStaticImport() {
		return staticImport;
	}

	public boolean isUsingWildcard() {
		return usesWildcard;
	}

	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("import ");
		if (this.staticImport) {
			b.append("static ");
		}
		b.append(name);
		if (this.usesWildcard) {
			b.append(".*");
		}
		b.append(';');
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (staticImport ? 1231 : 1237);
		result = prime * result + (usesWildcard ? 1231 : 1237);
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
		ImportDeclaration other = (ImportDeclaration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (staticImport != other.staticImport)
			return false;
		if (usesWildcard != other.usesWildcard)
			return false;
		return true;
	}

}


package org.mandarax.dsl;

import static org.mandarax.dsl.Utils.*;
/**
 * Represents variable declarations consisting of a type (name) and a variable name.
 * @author jens dietrich
 */
public class VariableDeclaration extends ASTNode {

	public VariableDeclaration(Position position, Context context,String type,String name) {
		super(position, context);
		this.type = type;
		this.name = name;
	}

	private String type = null;
	private String name = null;
	
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(type);
		b.append(' ');
		b.append(name);
		return b.toString();
	}
	
	public String getDefaultValue() {
		return Utils.getDefaultValue(type);
	}

}


package org.mandarax.dsl;

/**
 * Represents the declaration of an object. 
 * @author jens dietrich
 */
public class ObjectDeclaration extends ASTNode {
	
	private String type = null;
	private String name = null;
	private Expression defaultValueDeclaration = null;
	
	public ObjectDeclaration(Position position, Context context, String type,final String name,Expression defaultValueDeclaration) throws InternalScriptException {
		super(position, context);
		this.type = type;
		this.name = name;
		this.defaultValueDeclaration = defaultValueDeclaration;
		
		// add to context and check whether all variables are defined
//		Collection<ObjectDeclaration> defined = Collections2.filter(context.getObjectDeclarations(),new Predicate<ObjectDeclaration>(){
//			@Override
//			public boolean apply(ObjectDeclaration decl) {
//				return name.equals(decl.name);
//			}});
//		
//		if (!defined.isEmpty()) {
//			throw new InternalScriptException("Cannot define object " + name + " at " + position + " - this name has already been used to define an object at " + defined.iterator().next().getPosition());
//		}
		
		// check whether there are references to undefined variables
//		Collection<Variable> vars = defaultValueDeclaration.getVariables();
//		Collection<String> names = Collections2.transform(context.getObjectDeclarations(),new Function<ObjectDeclaration,String>(){
//			@Override
//			public String apply(ObjectDeclaration decl) {
//				return decl.name;
//			}});
//		
//		for (Variable var:vars) {
//			if (!names.contains(var)) {
//				throw new InternalScriptException("Cannot reference object " + var.getName() + " at " + var.getPosition() + " - this object has not yet been defined");
//			}
//		}
		
		// add to context
		context.add(this);
		
	}


	public void accept(ASTVisitor visitor) {
		if (visitor.visit(this)) {
			defaultValueDeclaration.accept(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("object ");
		b.append(type);
		b.append(' ');
		b.append(name);
		if (defaultValueDeclaration!=null) {
			b.append(" = ");
			b.append(defaultValueDeclaration);
		}
		b.append(";");
		return b.toString();
	}

	public String getName() {
		return name;
	}


	public Expression getDefaultValueDeclaration() {
		return defaultValueDeclaration;
	}

	public String getType() {
		return type;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((defaultValueDeclaration == null) ? 0
						: defaultValueDeclaration.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ObjectDeclaration other = (ObjectDeclaration) obj;
		if (defaultValueDeclaration == null) {
			if (other.defaultValueDeclaration != null)
				return false;
		} else if (!defaultValueDeclaration
				.equals(other.defaultValueDeclaration))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


}

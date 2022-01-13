
package org.mandarax.dsl;

import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;

/**
 * Represents annotations.
 * @author jens dietrich
 */
public class Annotation extends ASTNode {
	
	private String key = null;
	private String value = null;
	
	public Annotation(Position position, Context context, String key,String value) {
		super(position, context);
		this.key = key;
		this.value = unescapeJava(value);
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append('@');
		b.append(key);
		b.append("\"");
		b.append(value);
		b.append("\"");
		return b.toString();
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}

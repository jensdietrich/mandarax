package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Annotatable node.
 * @author jens dietrich
 */
public abstract class AnnotatableNode extends ASTNode {
	
	public AnnotatableNode(Position position, Context context) {
		super(position, context);
	}
	private List<Annotation> annotations = new ArrayList<Annotation>();
	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
	}
	public void addAnnotations(List<Annotation> list) {
		annotations.addAll(list);
	}
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	
	public boolean isAnnotated() {
		List<Annotation> annotations = getAnnotations();
		return annotations==null || annotations.size()==0;
	}
	@Override
	protected void copyPropertiesTo(ASTNode node) {
		super.copyPropertiesTo(node);
		if (node instanceof AnnotatableNode) {
			AnnotatableNode aNode = (AnnotatableNode)node;
			for (Annotation annotation:annotations) {
				aNode.addAnnotation(new Annotation(annotation.getPosition(),annotation.getContext(),annotation.getKey(),annotation.getValue()));
			}
		}
	}
}

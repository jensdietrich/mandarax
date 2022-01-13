package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for built-in relationships.
 * @author jens dietrich
 */
public class BuiltIns {
	
	public static RelationshipDefinition[]  getBuiltInRels() {
		return new RelationshipDefinition[]{
			createInDomainRel()
		};
	}
	public static RelationshipDefinition createInDomainRel() {
		
		Context context = new Context(); 
		context.add(new ImportDeclaration(Position.NO_POSITION,context,org.mandarax.rt._InDomainRel.class.getPackage().getName(),false,false));
		
		List<VariableDeclaration> slots = new ArrayList<VariableDeclaration>(2);
		VariableDeclaration v1 = new VariableDeclaration(Position.NO_POSITION,context,Object.class.getName(),"element");
		VariableDeclaration v2 = new VariableDeclaration(Position.NO_POSITION,context,Iterable.class.getName(),"container");
		slots.add(v1);
		slots.add(v2);
		
		List<FunctionDeclaration> queries = new ArrayList<FunctionDeclaration>();
		FunctionDeclaration q1 = new FunctionDeclaration(Position.NO_POSITION,context,Visibility.PUBLIC,"getElements","container");
		FunctionDeclaration q2 = new FunctionDeclaration(Position.NO_POSITION,context,Visibility.PUBLIC,"contains","container","element");
		queries.add(q1);
		queries.add(q2);
		
		RelationshipDefinition rel = new RelationshipDefinition(Position.NO_POSITION,context,"_InDomain",slots,new ArrayList<String>(0),queries);
		rel.setTypeSafe(false);
		return rel;
		
	}
}

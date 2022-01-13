
package org.mandarax.compiler.impl;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.mandarax.dsl.FunctionDeclaration;
import org.mandarax.dsl.RelationshipDefinition;
import org.mandarax.dsl.Variable;
import org.mandarax.dsl.util.Resolver;


/**
 * Utilities.
 * @author jens dietrich
 */
public class CompilerUtils {
	public static final DateFormat DEFAULT_DATE_FORMAT = DateFormat.getDateTimeInstance();
	public static final DateFormat VERSION_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	public static String getTimestamp() {
		return DEFAULT_DATE_FORMAT.format(new Date());
	}
	
	public static String getTimestampAsVersion() {
		return VERSION_DATE_FORMAT.format(new Date());
	}
	// used in templates to iterate over lists by index
	public static List<Integer> getIndices(List<? extends Object> list) {
		List<Integer> indices = new ArrayList<Integer>(list.size());
		for (int i=0;i<list.size();i++) indices.add(i);
		return indices;
	}
	// get the variable names
	// try to retain the order, but avoid duplicates
	public static List<String> getNames(List<Variable> variables) {
		List<String> names =  new ArrayList<String>();
		for (Variable var:variables) {
			if (!names.contains(var.getName())) names.add(var.getName());
		}
		return names;
	}
	// method used in template for external facts to fins matching method for query 
	public static Method findQueryMethod(FunctionDeclaration query,Class<?> iterableType,Resolver resolver) throws Exception {
		@SuppressWarnings("rawtypes")
		RelationshipDefinition rel = query.getRelationship();
		Class[] paramTypes = new Class[query.getParameterNames().size()];
		for (int i=0;i<paramTypes.length;i++) {
			paramTypes[i] = resolver.getType(query.getContext(),rel.getTypeNameForSlot(query.getParameterNames().get(i)));
		}
		try {
			Method m = iterableType.getMethod(query.getName(), paramTypes);
			DefaultCompiler.LOGGER.warn("Found method to optimize access to external fact set: "  + m);
			return m;
		}
		catch (Exception x) {
			DefaultCompiler.LOGGER.warn("Cannot found method to optimize access to external fact set, will use iterator() and apply filter - "  + x.getClass().getName() + ": " + x.getMessage());
			return null;
		}
	}
}

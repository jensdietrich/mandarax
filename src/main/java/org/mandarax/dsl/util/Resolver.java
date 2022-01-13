package org.mandarax.dsl.util;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.mandarax.dsl.Context;
/**
 * Utility to map names used in scripts to classes and types that can be analysed and invoked using reflection.
 * @author jens dietrich
 */
public interface Resolver {
	/**
	 * Get the member for the given name. 
	 * <b>postcondition: The member should be either a Method or a Field. </b>
	 * The reason to have a general method covering both fields and methods is to treat properties transparently: 
	 * a property used field syntax, but is represented by a getter method.
	 * @param context the context (that has the imports)
	 * @param name the member name
	 * @param className the name of the type that owns the member
	 * @param paramTypeNames the array is null if this is a field reference!
	 * @return a field or method
	 */
	Member getMember(Context context,String name,String className,String... paramTypeNames) throws ResolverException ;
	/**
	 * Get the class for the given name. 
	 * @param context the context (that has the imports)
	 * @param name the member name
	 * @return a class
	 */
	Class getType(Context context,String name) throws ResolverException ;
	
	/**
	 * Get the function for the given name. 
	 * A function is a static method. 
	 * <b>postcondition: The method should be static. </b>
	 * @param context the context (that has the imports)
	 * @param name the function name
	 * @param paramTypeNames the array is null if this is a field reference!
	 * @return a static method
	 */
	Method getFunction(Context context,String name,String... paramTypeNames) throws ResolverException ;
	
	/**
	 * Get the function for the given name. The param types are not known, only the number of parameters. 
	 * A function is a static method. 
	 * Functions are defined externally, and imported using static imports. 
	 * <b>postcondition: The method should be static. </b>
	 * @param context the context (that has the imports)
	 * @param name the function name
	 * @param paramCount the number of parameters
	 * @return a static method
	 */
	public Method getFunction(Context context,String name,int paramCount) throws ResolverException ;
}	

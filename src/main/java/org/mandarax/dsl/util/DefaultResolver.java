
package org.mandarax.dsl.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.mandarax.dsl.Context;
import org.mandarax.dsl.ImportDeclaration;

/**
 * Default resolver. Will try to resolve missing field references using property getters defined using the Java Beans spec.
 * E.g., in obj.firstName , if there is no public field firstName, the resolver will try to find the method getFirstName().
 * @author jens dietrich
 */

public class DefaultResolver implements Resolver {
	
	private ClassLoader classloader = null;
	private static Logger LOGGER = Logger.getLogger(DefaultResolver.class);

	public DefaultResolver(ClassLoader classloader) {
		super();
		this.classloader = classloader;
	}
	public DefaultResolver() {
		super();
	}

	@Override
	public Member getMember(Context context,String name, String className,String... paramTypeNames) throws ResolverException {
		Class clazz = getType(context,className);
		boolean isFieldRef = paramTypeNames==null;
		if (isFieldRef) {
			Field field = null;
			try {
				field = clazz.getField(name);
			} catch (Exception e) {
				// LOGGER.error(e);
			}
			if (field!=null && Modifier.isPublic(field.getModifiers())) {
				LOGGER.debug("Resolving feature " +name + " in " + className + " with parameters " + paramTypeNames + " to " + field);
				return field;
			}
			
			// try to use bean framework
			Exception x = null;
			try {
				PropertyDescriptor[] properties = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
				for (PropertyDescriptor p:properties) {
					if (p.getName().equals(name)) {
						Method reader = p.getReadMethod();
						if (reader!=null) {
							LOGGER.debug("Resolving feature " +name + " in " + className + " with parameters " + paramTypeNames + " to " + reader);
							return reader;
						}
						else {
							break;
						}
					}
				}
			} catch (IntrospectionException e) {
				x = e;
			}
			// try to locate method with exact name: this will map property hasWife to method hasWife() instead of getHasWife()
			try {
				@SuppressWarnings("unchecked")
				Method m = clazz.getMethod(name,new Class[]{});
				if (m!=null && Modifier.isPublic(m.getModifiers())) {
					LOGGER.debug("Resolving feature " +name + " in " + className + " with parameters " + paramTypeNames + " to " + m);
					return m;
				}
			}
			catch (Exception e2) {
				x=e2;
			}

			throw new ResolverException("Cannot resolve property " + name + " in "+ clazz.getName(),x);
		}
		
		// method access
		Class[] paramTypes = new Class[paramTypeNames.length];
		for (int i=0;i<paramTypeNames.length;i++) {
			paramTypes[i] = getType(context,paramTypeNames[i]);
		}
		
		try {
			Method m = MethodUtils.getMatchingAccessibleMethod(clazz,name,paramTypes);
			if (Modifier.isPublic(m.getModifiers())) {
				LOGGER.debug("Resolving feature " +name + " in " + className + " with parameters " + paramTypeNames + " to " + m);
				return m;
			}
			else {
				throw new ResolverException("Method " + m + " in "+ clazz.getName() + " is not visible");
			}
		} catch (Exception e) {
			throw new ResolverException("Cannot resolve method " + name + " in "+ clazz.getName(),e);
		}
		
	}

	@Override
	public Class getType(Context context,String name) throws ResolverException {
		Class clazz = tryToLoadPrimitive(name);
		if (clazz!=null) {
			LOGGER.debug("Resolving class name " +name + " to " + clazz);
			return clazz;
		}
		
		clazz = tryToLoad(name);
		if (clazz!=null) {
			LOGGER.debug("Resolving class name " +name + " to " + clazz);
			return clazz;
		}
		
		// try to load class in the package defined in the context
		if (context.getPackageDeclaration()!=null) {
			clazz = tryToLoad(""+context.getPackageDeclaration().getName()+'.'+name);
			if (clazz!=null) {
				LOGGER.debug("Resolving class name " +name + " to " + clazz);
				return clazz;
			}
		}
		
		// try to load class from java.lang
		clazz = tryToLoad("java.lang."+name);
		if (clazz!=null) {
			LOGGER.debug("Resolving class name " +name + " to " + clazz);
			return clazz;
		}
		
		// try to load class from imported classes
		for (ImportDeclaration imp:context.getImportDeclarations()) {
			if (!imp.isUsingWildcard() && imp.getName().endsWith("."+name)) {
				clazz = tryToLoad(imp.getName());
				if (clazz!=null) {
					LOGGER.debug("Resolving class name " +name + " to " + clazz);
					return clazz;
				}
			}
		}

		// try to load class from imported packages
		for (ImportDeclaration imp:context.getImportDeclarations()) {
			if (imp.isUsingWildcard()) {
				clazz = tryToLoad(imp.getName()+'.'+name);
				if (clazz!=null) {
					LOGGER.debug("Resolving class name " +name + " to " + clazz);
					return clazz;
				}
			}
		}
		
		// nothing worked - throw exception	
		LOGGER.warn("Cannot resolve type " +name);
		throw new ResolverException("Cannot find class " + name);
	}
	
	private Class tryToLoadPrimitive(String name) throws ResolverException  {
		try {
			return ClassUtils.getClass(name);
		}
		catch (Exception x) {
			return null;
		}
	}
	private Class tryToLoad(String name) {
		try {
			return (classloader==null)?Class.forName(name):classloader.loadClass(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	
	/**
	 * Get the function for the given name. 
	 * A function is a static method. 
	 * Functions are defined externally, and imported using static imports. 
	 * <b>postcondition: The method should be static. </b>
	 * @param context the context (that has the imports)
	 * @param name the function name
	 * @param paramTypeNames the array is null if this is a field reference!
	 * @return a static method
	 */
	public Method getFunction(Context context,String name,String... paramTypeNames) throws ResolverException {
		Method function = null;
		Class clazz = null;
		
		Class[] paramTypes = new Class[paramTypeNames.length];
		for (int i=0;i<paramTypeNames.length;i++) {
			try {
				paramTypes[i]=getType(context,paramTypeNames[i]);
			}
			catch (Exception x) {
				throw new ResolverException("Cannot find method " + name + " with parameters " + paramTypeNames);
			}
		}
		
		// try to load method from imported classes
		for (ImportDeclaration imp:context.getStaticImportDeclarations()) {
			if (!imp.isUsingWildcard() && imp.getName().endsWith("."+name)) {
				String className = imp.getName().substring(0,imp.getName().lastIndexOf(name)-1);
				return tryToLoadStaticMethod(context,name,className,paramTypes);
			}
		}
		
		// try to load method from imported packages
		for (ImportDeclaration imp:context.getStaticImportDeclarations()) {
			if (imp.isUsingWildcard()) {
				return tryToLoadStaticMethod(context,name,imp.getName(),paramTypes);
			}
		}
		
		throw new ResolverException("Cannot resolve static method " + name);
	}
	
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
	public Method getFunction(Context context,String name,int paramCount) throws ResolverException {
		Method function = null;
		Class clazz = null;

		
		// try to load method from imported classes
		for (ImportDeclaration imp:context.getStaticImportDeclarations()) {
			if (!imp.isUsingWildcard() && imp.getName().endsWith("."+name)) {
				String className = imp.getName().substring(0,imp.getName().lastIndexOf(name)-1);
				return tryToLoadStaticMethod(context,name,className,paramCount);
			}
		}
		
		// try to load method from imported packages
		for (ImportDeclaration imp:context.getStaticImportDeclarations()) {
			if (imp.isUsingWildcard()) {
				return tryToLoadStaticMethod(context,name,imp.getName(),paramCount);
			}
		}
		
		throw new ResolverException("Cannot resolve static method " + name);
	}
	
	
	private Method tryToLoadMethod(Context context,String name,String className,Class[] paramTypes) throws ResolverException {
		Class clazz = null;
		Method method = null;
		try {
			clazz = getType(context,className);
		}
		catch (Exception x) {
			throw new ResolverException("Cannot find class that defines method " + name,x);
		}
		try {
			method = clazz.getMethod(name, paramTypes);
		}
		catch (Exception x) {
			throw new ResolverException("Cannot find method " + name,x);
		}
		
		if (Modifier.isPublic(method.getModifiers())) {
			return method;
		}
		else {
			throw new ResolverException("Method " + method + " in "+ clazz.getName() + " is not visible");
		}
	}
	
	private Method tryToLoadStaticMethod(Context context,String name,String className,int paramCount) throws ResolverException {
		Class clazz = null;
		Method method = null;
		try {
			clazz = getType(context,className);
		}
		catch (Exception x) {
			throw new ResolverException("Cannot find class that defines method " + name,x);
		}
		try {
			for (Method m:clazz.getMethods()) {
				if (Modifier.isStatic(m.getModifiers()) && m.getName().equals(name) && m.getParameterTypes().length==paramCount) {
					method = m;
					break;
				}
			}
		}
		catch (Exception x) {
			throw new ResolverException("Cannot find method " + name,x);
		}
		
		if (Modifier.isPublic(method.getModifiers())) {
			return method;
		}
		else {
			throw new ResolverException("Method " + method + " in "+ clazz.getName() + " is not visible");
		}
	}
	
	
	private Method tryToLoadStaticMethod(Context context,String name,String className,Class[] paramTypes) throws ResolverException {
		Method function = tryToLoadMethod(context,name,className,paramTypes);
		if (!Modifier.isStatic(function.getModifiers())) {
			throw new ResolverException("Imported functions should be static, but this one is not: " + name);
		}
		return function;
	}
	

}

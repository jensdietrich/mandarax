package org.mandarax.compiler;

import java.io.*;
/**
 * Interface describing how to access source code and compiled classes.
 * @author jens dietrich
 */
public interface Location {
	/**
	 * Get a stream to write source code.
	 * @param packageName the package name
	 * @param className the class name
	 * @return a Writer using the specified class as destination
	 * @throws CompilerException if the Writer couldn't be created 
	 */
	public Writer getSrcOut(String packageName,String className) throws CompilerException;
		
	
}

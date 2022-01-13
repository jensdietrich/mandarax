package org.mandarax.compiler;

import org.mandarax.MandaraxException;
import org.mandarax.dsl.CompilationUnit;
import org.mandarax.dsl.Position;

/**
 * Class used to report compiler exceptions
 * @author jens dietrich
 */

@SuppressWarnings("serial")
public class CompilerException extends MandaraxException {
	

	public CompilerException() {}

	public CompilerException(CompilationUnit cu,Position pos,String message) {
		super(buildMessage(cu,pos,message));
	}

	public CompilerException(Throwable cause) {
		super(cause);
	}

	public CompilerException(CompilationUnit cu,Position pos,String message, Throwable cause) {
		super(buildMessage(cu,pos,message), cause);
	}
	
	public CompilerException(String message) {
		super(message);
	}
	
	public CompilerException(String message, Throwable cause) {
		super(message, cause);
	}

	private static String buildMessage(CompilationUnit cu,Position pos,String m) {
		return "Compiler error in " + cu + " at position " + pos + ": " + m;
	}
}

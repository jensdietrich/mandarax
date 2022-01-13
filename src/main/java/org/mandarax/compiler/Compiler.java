package org.mandarax.compiler;

import java.io.File;
import java.net.URL;
import org.mandarax.MandaraxException;
import org.mandarax.dsl.parser.ScriptException;
/**
 * Compiler interface.
 * @author jens dietrich
 */
public interface Compiler {
	
	
	/**
	 * Compile the sources.
	 * @param target the location describing where the source code and byte code generated will be stored
	 * @param mode the compilation mode
	 * @param sources the sources where the rel resources can be found
	 * @throws CompilerException
	 * @throws ScriptException
	 */
	public void compile(Location target,CompilationMode mode,URL... sources) throws MandaraxException  ; 
	
	/**
	 * Compile the sources.
	 * @param target the location describing where the source code and byte code generated will be stored
	 * @param mode the compilation mode
	 * @param sources the files where the rel resources can be found, this should be either *.rel files or directories, directories will be searched recursively 
	 * @throws CompilerException
	 * @throws ScriptException
	 */
	public void compile(Location target,CompilationMode mode,File... sources) throws MandaraxException  ; 
	/**
	 * Compile the sources.
	 * @param target the location describing where the source code and byte code generated will be stored
	 * @param mode the compilation mode
	 * @param sources the sources where the rel resources can be found
	 * @throws CompilerException
	 * @throws ScriptException
	 */
	public void compile(Location target,CompilationMode mode,Source... sources) throws MandaraxException  ; 
	
}

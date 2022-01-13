
package org.mandarax.examples.userv.scripts;

import java.io.File;
import java.io.FileFilter;
import org.apache.log4j.Logger;
import org.mandarax.compiler.CompilationMode;
import org.mandarax.compiler.Compiler;
import org.mandarax.compiler.Location;
import org.mandarax.compiler.impl.DefaultCompiler;
import org.mandarax.compiler.impl.FileSystemLocation;

/**
 * Utility to generate code for the userv rules.
 * @author jens dietrich
 */
public class GenerateClassesForRules {
	
	public static final Logger LOGGER = Logger.getLogger(GenerateCodeForTesting.class);

	public static void main(String[] args) throws Exception {
		File[] files = new File("src/userv/org/mandarax/examples/userv/rules").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().endsWith(".rel");
			}});
		
		compile(files);
		
	}

	private static void compile(File[] files) throws Exception {
		Compiler compiler = new DefaultCompiler();
		Location location = new FileSystemLocation(new File("src/userv/"));
		compiler.compile(location,CompilationMode.RELATIONSHIP_TYPES,files);
		compiler.compile(location,CompilationMode.QUERIES,files);
		
		LOGGER.info("Compiled rules from " + files);
	}
}

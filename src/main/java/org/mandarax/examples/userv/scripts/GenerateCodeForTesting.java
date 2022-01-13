
package org.mandarax.examples.userv.scripts;

import java.io.*;

import org.apache.log4j.Logger;
import org.mandarax.compiler.CompilationMode;
import org.mandarax.compiler.Compiler;
import org.mandarax.compiler.Location;
import org.mandarax.compiler.impl.DefaultCompiler;
import org.mandarax.compiler.impl.FileSystemLocation;

/**
 * Generates code for all test cases.
 * @author jens dietrich
 */
public class GenerateCodeForTesting {

	public static final Logger LOGGER = Logger.getLogger(GenerateCodeForTesting.class);

	public static void main(String[] args) throws Exception {
//		for (File file:new File("src/testcases/test/org/mandarax/compiler").listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File pathname) {
//				return pathname.getAbsolutePath().endsWith(".rel");
//			}})) {
//			
//			LOGGER.info("generating code for " + file.getAbsolutePath());
//			compile(file);
//		}
		
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef4.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef5.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef6.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef7.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef8.rel"));
		// redef9 does not compile (and is not supposed to), see TestScheduler
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef10.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef11.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef12.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef13.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldef14.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefNAF1.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefFactorial.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefRList.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefInDomain1.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefAggregation1.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefStaticMethods1.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefStaticMethods2.rel"));
		
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefAggregation2.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefExternalFacts1.rel"));
		compile(new File("src/testcases/test/org/mandarax/compiler/reldefExternalFacts2.rel"));
	}

	private static void compile(File file) throws Exception {
		Compiler compiler = new DefaultCompiler();
		Location location = new FileSystemLocation(new File("src-generated/testcases/"));	
		compiler.compile(location,CompilationMode.RELATIONSHIP_TYPES,file);
		compiler.compile(location,CompilationMode.QUERIES,file);
		
		LOGGER.info("Compiled rules from " + file);
	}

}

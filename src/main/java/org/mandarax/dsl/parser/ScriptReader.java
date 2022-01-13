
package org.mandarax.dsl.parser;

import java.io.IOException;
import java.io.InputStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.mandarax.dsl.*;

/**
 * Utility to read artefacts from scripts.
 * @author jens dietrich
 */
public class ScriptReader {
	private MandaraxParser getParser(InputStream in) throws ScriptException {
		MandaraxLexer lexer = null;
		try {
			lexer = new MandaraxLexer(new ANTLRInputStream(in));
		} catch (IOException e) {
			throw new ScriptException(e);
		}
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new MandaraxParser(tokens);		
	}
	public Expression readExpression(InputStream in) throws ScriptException {
		try {
			return getParser(in).expression().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	public ImportDeclaration readImportDeclaration(InputStream in) throws ScriptException {
		try {
			return getParser(in).importDeclaration().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	public RelationshipDefinition readRelationshipDefinition(InputStream in) throws ScriptException {
		try {
			return getParser(in).relationshipDefinition().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public PackageDeclaration readPackageDeclaration(InputStream in) throws ScriptException {
		try {
			return getParser(in).packageDeclaration().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public Rule readRule(InputStream in) throws ScriptException {
		try {
			return getParser(in).rule().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public ExternalFacts readExternalFacts(InputStream in) throws ScriptException {
		try {
			return getParser(in).external().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public Annotation readAnnotation(InputStream in) throws ScriptException {
		try {
			return getParser(in).annotation().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public ObjectDeclaration readObjectDeclaration(InputStream in) throws ScriptException {
		try {
			return getParser(in).objectDeclaration().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public CompilationUnit readCompilationUnit(InputStream in) throws ScriptException {
		try {
			return getParser(in).compilationUnit().value;
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	
}



package org.mandarax.dsl.parser;

import org.antlr.runtime.RecognitionException;
import org.mandarax.dsl.InternalScriptException;
import org.mandarax.dsl.Position;

/**
 * Error handler used by lexer and parser.
 * @author jens dietrich
 */
public class ErrorReporter {

    public void handleError(String message,RecognitionException e) {
  
      	throw new InternalScriptException("Parser error at " + new Position(e.line,e.charPositionInLine) + " - " + message,e);
    }
    
}

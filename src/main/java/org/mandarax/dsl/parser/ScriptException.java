
package org.mandarax.dsl.parser;

import org.mandarax.MandaraxException;

/**
 * Exception encountered when parsing scripts.
 * @author jens dietrich
 */
public class ScriptException extends MandaraxException {

	public ScriptException() {}

	public ScriptException(String message) {
		super(message);
	}

	public ScriptException(Throwable cause) {
		super(cause);
	}

	public ScriptException(String message, Throwable cause) {
		super(message, cause);
	}

}

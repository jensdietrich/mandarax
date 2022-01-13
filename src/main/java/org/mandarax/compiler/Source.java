package org.mandarax.compiler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract interface describing locations from where relationship definitions can be read.
 * Can be easily implemented by wrapping files, URLs, memory resources (like strings) or existing streams. 
 * @author jens dietrich
 */
public interface Source {
	public InputStream openStream() throws IOException ; 
	// a descriptive name (file name, URL, ..)
	public String getName();
	
}

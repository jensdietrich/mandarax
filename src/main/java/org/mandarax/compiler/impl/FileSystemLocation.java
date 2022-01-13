package org.mandarax.compiler.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.mandarax.compiler.CompilerException;
import org.mandarax.compiler.Location;

/**
 * Location implementation used to output generated code to the file system.
 * @author jens dietrich
 */
public class FileSystemLocation implements Location {

	public FileSystemLocation(File folder) {
		super();
		this.folder = folder;
		if (folder==null) throw new IllegalArgumentException("Folder cannot be null");
		if (!folder.exists()) folder.mkdirs();
	}

	private File folder = null;
	
	@Override
	public Writer getSrcOut(String packageName, String className) throws CompilerException {

			String relFolder = packageName.replace('.','/');
			File dir = new File(folder,relFolder);
			if (!dir.exists()) dir.mkdirs();
			File file = new File(dir,className + ".java");
			try {
				return new FileWriter(file);
			} catch (IOException e) {
				throw new CompilerException(e);
			}
	}

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

}

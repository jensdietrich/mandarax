
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to keep track of contextual information such as namespace definitions and defined objects.
 * @author jens dietrich
 */

public class Context {
	private PackageDeclaration packageDeclaration = null;
	private List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
	private List<ImportDeclaration> staticImports = new ArrayList<ImportDeclaration>();
	private List<ObjectDeclaration> objectDeclarations = new ArrayList<ObjectDeclaration>();
	
	public List<ImportDeclaration> getImportDeclarations() {
		return imports;
	}
	public PackageDeclaration getPackageDeclaration() {
		return packageDeclaration;
	}
	public List<ImportDeclaration> getStaticImportDeclarations() {
		return staticImports;
	}
	public List<ObjectDeclaration> getObjectDeclarations() {
		return objectDeclarations;
	}
	public void add(ImportDeclaration imp) {
		if (imp.isStaticImport()) this.staticImports.add(imp);
		else this.imports.add(imp);
	}
	public void add(ObjectDeclaration decl) {
		objectDeclarations.add(decl);
	}
	public void setPackageDeclaration(PackageDeclaration packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime
				* result
				+ ((objectDeclarations == null) ? 0 : objectDeclarations
						.hashCode());
		result = prime
				* result
				+ ((packageDeclaration == null) ? 0 : packageDeclaration
						.hashCode());
		result = prime * result
				+ ((staticImports == null) ? 0 : staticImports.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Context other = (Context) obj;
		if (imports == null) {
			if (other.imports != null)
				return false;
		} else if (!imports.equals(other.imports))
			return false;
		if (objectDeclarations == null) {
			if (other.objectDeclarations != null)
				return false;
		} else if (!objectDeclarations.equals(other.objectDeclarations))
			return false;
		if (packageDeclaration == null) {
			if (other.packageDeclaration != null)
				return false;
		} else if (!packageDeclaration.equals(other.packageDeclaration))
			return false;
		if (staticImports == null) {
			if (other.staticImports != null)
				return false;
		} else if (!staticImports.equals(other.staticImports))
			return false;
		return true;
	}
}

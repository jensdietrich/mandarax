
package org.mandarax.rt;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * Represents one node in the derivation.
 * Contains the name of the artefact used (can be used to look up rules in the kb),
 * and an int defining what kind of object this is.
 * See the constants in DerivationController for possible values.
 * @author jens dietrich
 */

public class DerivationLogEntry {
	private String name = null;
	private int kind = 0;
	private Properties annotations = null;
	public DerivationLogEntry(String name, int kind,Properties annotations) {
		super();
		this.name = name;
		this.kind = kind;
		this.annotations = annotations;
	}
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Object> getAnnotationKeys() {
		return Collections.unmodifiableSet(annotations.keySet());
	}
	
	public String getAnnotation(String key) {
		return annotations.getProperty(key);
	}

	/**
	 * Get the type of the node as string.
	 * @return a string
	 */
	public String getCategory() {
		switch (kind) {
			case DerivationController.RULE:return "rule";
			case DerivationController.FACT:return "fact";
			default: return "other";
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result + kind;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DerivationLogEntry other = (DerivationLogEntry) obj;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		if (kind != other.kind)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override 
	public String toString() {
		return name;
	}
}

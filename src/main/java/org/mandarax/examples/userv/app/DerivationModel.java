
package org.mandarax.examples.userv.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.mandarax.rt.DerivationLogEntry;


/**
 * Tree model used to visualise the rules used and their annotations.
 * @author jens dietrich
 */

public class DerivationModel  implements TreeModel {
	private List<DerivationLogEntry> derivationLog = new ArrayList<DerivationLogEntry>();
	private Map<DerivationLogEntry,List<Annotation>> annotations = new HashMap<DerivationLogEntry,List<Annotation>>();
	
	class Annotation {
		String key,value;
		public Annotation(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}
		public String toString() {
			return key+": "+value;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			Annotation other = (Annotation) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		private DerivationModel getOuterType() {
			return DerivationModel.this;
		}
	}
	
	public DerivationModel(List<DerivationLogEntry> log) {
		
		this.derivationLog = log;
		
		// initialise map
		for (DerivationLogEntry entry:log) {
			Collection keys = entry.getAnnotationKeys();
			List<String> l = new ArrayList<String>();
			for (Object k:keys) l.add(k.toString());  // keys are strings, this can be safely done
			Collections.sort(l);
			List<Annotation> annotationList = new ArrayList<Annotation>();
			for (String key:l) {
				Annotation annotation = new Annotation(key,entry.getAnnotation(key));
				annotationList.add(annotation);
				annotations.put(entry,annotationList);
			}

		}
		
	}


	public void addTreeModelListener(TreeModelListener l) {
	}

	public Object getChild(Object parent, int index) {
		if (parent==derivationLog) {
			return derivationLog.get(index);
		}
		else if (parent instanceof DerivationLogEntry) {
			return this.annotations.get((DerivationLogEntry)parent).get(index);
		}
		return null;
	}

	public int getChildCount(Object parent) {
		if (parent==derivationLog) {
			return derivationLog.size();
		}
		else if (parent instanceof DerivationLogEntry) { 
			return this.annotations.get((DerivationLogEntry)parent).size();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent==derivationLog)
			return derivationLog.indexOf(child);
		else if (parent instanceof DerivationLogEntry) {
			return this.annotations.get((DerivationLogEntry)parent).indexOf(child);
		}
		return 0;
	}
	

	@Override
	public Object getRoot() {
		return derivationLog;
	}

	public boolean isLeaf(Object node) {
		return this.getChildCount(node)==0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}


}

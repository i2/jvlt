package net.sourceforge.jvlt;

public class Sense implements Comparable<Sense>, Reinitializable {
	public static class Comparator implements java.util.Comparator<Sense> {
		public int compare(Sense s1, Sense s2) {
			if (! s1._translation.equals(s2._translation))
				return s1._translation.compareTo(s2._translation);
			else
				return s1._definition.compareTo(s2._definition);
		}
		
		public boolean equals(Object obj) { return super.equals(obj); }
	}
	
	private String _translation;
	private String _definition;
	private Entry _parent;

	public Sense (String translation, String definition, Entry parent) {
		_translation = translation;
		_definition = definition;
		_parent = parent;
	}
	
	public Sense (String translation, String definition) {
		this(translation, definition, null); }

	public Sense() { this ("", "", null); }
	
	public void reinit(Reinitializable obj) {
		Sense sense = (Sense) obj;
		_translation = sense._translation;
		_definition = sense._definition;
		// Do not copy "_parent" because this attribute should not be changed.
	}
	
	public String getTranslation () { return _translation; }

	public String getDefinition() { return _definition; }

	public Entry getParent() { return _parent; }

	public String getID() {
		if (_parent == null)
			return null;
		
		String id = _parent.getID() + "-s";
		Sense[] senses = _parent.getSenses();
		for (int i=0; i<senses.length; i++)
			if (senses[i].equals(this))
				return id + (i+1);
		
		return null;
	}
	
	public boolean equals (Object o) {
		if (! (o instanceof Sense))
			return false;
		
		return (compareTo((Sense) o) == 0);
	}

	public void setTranslation (String val) { _translation = val; }

	public void setDefinition (String val) { _definition = val; }

	public void setParent (Entry parent) { _parent = parent; }

	public int compareTo (Sense s) {
		if (s == null)
			return 1;
		
		if (! _parent.getID().equals(s.getParent().getID()))
			return _parent.getID().compareTo(s.getParent().getID());
		if (! _translation.equals(s.getTranslation()))
			return _translation.compareTo(s.getTranslation());
		else if (! _definition.equals(s.getDefinition()))
			return _definition.compareTo(s.getDefinition());
		else
			return 0;
	}
	
	public String toString() {
		String str="";
		if (_definition != null && !_definition.equals(""))
			str += "(" + _definition + ")";
		
		if (_translation != null && !_translation.equals("")) {
			if (! str.equals(""))
				str += ", ";
			
			str += _translation;
		}

		return str;
	}
	
	public Object clone() {
		try { return super.clone();	}
		catch (CloneNotSupportedException ex) {
			return null; // Should not happen.
		}
	}
}


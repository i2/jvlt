package net.sourceforge.jvlt;

import java.util.ArrayList;

public class AttributeChoice implements Comparable<AttributeChoice> {
	private String _name;
	private AttributeChoice _parent = null;
	private ArrayList<AttributeChoice> _children = null;

	public AttributeChoice(String name) {
		_name = name;
		_children = new ArrayList<AttributeChoice>();
	}
	
	public String getName() { return _name; }
	
	public AttributeChoice getParent() { return _parent; }
	
	public AttributeChoice[] getChildren() {
		return _children.toArray(new AttributeChoice[0]);
	}
	
	public void setParent(AttributeChoice parent) {
		_parent = parent;
		parent._children.add(this);
	}
	
	public int compareTo(AttributeChoice o) {
		if (o == null)
			return -1;
		else
			return _name.compareTo(o._name);
	}
	
	public boolean equals(Object o) {
		return compareTo((AttributeChoice) o)==0;
	}
		
	public String toString() { return _name; }
}

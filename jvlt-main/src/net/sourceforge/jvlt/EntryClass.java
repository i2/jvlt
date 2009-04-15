package net.sourceforge.jvlt;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class EntryClass implements Comparable<EntryClass> {
	private String _name = null;
	private EntryClass _parent_class = null;
	private Vector<SchemaAttribute> _attributes = new Vector<SchemaAttribute>();
	private Vector<String> _groups = new Vector<String>();
	// Maps groups to vectors of attributes
	private TreeMap<String, Vector<SchemaAttribute>> _group_map;
	// Maps names to attributes
	private TreeMap<String, SchemaAttribute> _name_map;
	private TreeSet<EntryClass> _children = new TreeSet<EntryClass>();
	
	public EntryClass(String name) {
		_name = name;
		_group_map = new TreeMap<String, Vector<SchemaAttribute>>();
		_name_map = new TreeMap<String, SchemaAttribute>();
	}
	
	public String getName() { return _name; }
	
	public String[] getGroups() {
		return (String[]) _groups.toArray(new String[0]);
	}
	
	public SchemaAttribute[] getAttributes() {
		return _attributes.toArray(new SchemaAttribute[0]);
	}
	
	/**
	 * Return the attributes for a certain group.
	 * If the argument group is null or the empty string, the attributes
	 * not being assigned to a group are returned. */
	public SchemaAttribute[] getAttributes(String group) {
		Vector<SchemaAttribute> atts = _group_map.get(group);
		if (atts == null)
			return new SchemaAttribute[0];
		else
			return (SchemaAttribute[]) atts.toArray(new SchemaAttribute[0]);
	}
	
	public SchemaAttribute getAttribute(String name) {
		return (SchemaAttribute) _name_map.get(name);
	}
	
	/**
	 * Add an attribute.
	 * The attribute is only added if there does not already exist an
	 * attribute with the same name and the same group. */
	public void addAttribute(SchemaAttribute attr) {
		if (_name_map.containsKey(attr.getName()))
			return;
		
		String group = attr.getGroup();
		if (_group_map.containsKey(group)) {
			Vector<SchemaAttribute> vec = _group_map.get(group);
			vec.add(attr);
		}
		else {
			Vector<SchemaAttribute> vec = new Vector<SchemaAttribute>();
			vec.add(attr);
			_group_map.put(group, vec);
			_groups.add(group);
		}
		_name_map.put(attr.getName(), attr);
		_attributes.add(attr);
	}
	
	public void addAttributes(SchemaAttribute[] atts) {
		for (int i=0; i<atts.length; i++)
			addAttribute(atts[i]);
	}
	
	public EntryClass[] getChildClasses() {
		return _children.toArray(new EntryClass[0]);
	}
	
	public EntryClass getParentClass() { return _parent_class; }
	
	public void setParentClass(EntryClass cl) {
		_parent_class = cl;
		if (cl != null)
			_parent_class._children.add(this);
	}
	
	public Object clone() {
		EntryClass cl = new EntryClass(_name);
		Iterator<SchemaAttribute> it = _attributes.iterator();
		while(it.hasNext())
			cl.addAttribute((SchemaAttribute) it.next().clone());
		return cl;
	}

	public int compareTo(EntryClass o) {
		return _name.compareTo(((EntryClass) o)._name);
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		else
			return ((EntryClass) o)._name.equals(_name);
	}
	
	public int hashCode() { return _name.hashCode(); }
		
	public String toString() { return _name; }
}


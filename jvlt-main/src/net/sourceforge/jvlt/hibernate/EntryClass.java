package net.sourceforge.jvlt.hibernate;

import java.util.*;

public class EntryClass {
	private Long id = null;
	private String name = null;
	private Set<SchemaAttribute> attributes = new HashSet<SchemaAttribute>();
	
	public Long getId() { return this.id; }
	
	public String getName() { return this.name; }
	
	public Set<SchemaAttribute> getAttributes() { return this.attributes; }
	
	public void setId(Long id) { this.id = id; }
	
	public void setName(String name) { this.name = name; }
	
	public void setAttributes(Set<SchemaAttribute> a) { this.attributes = a; }
}

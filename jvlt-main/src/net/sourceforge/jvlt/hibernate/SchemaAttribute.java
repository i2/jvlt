package net.sourceforge.jvlt.hibernate;

public class SchemaAttribute {
	private Long id = null;
	private String name = null;
	
	public Long getId() { return this.id; }
	
	public String getName() { return this.name; }
	
	public void setId(Long id) { this.id = id; }
	
	public void setName(String name) { this.name = name; }
}

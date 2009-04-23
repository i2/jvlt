package net.sourceforge.jvlt.hibernate;

public class Sense {
	private Long id = null;
	private String translation = null;
	private String definition = null;
	
	public Long getId() { return this.id; }
	
	public String getTranslation() { return this.translation; }
	
	public String getDefinition() { return this.definition; }
	
	public void setId(Long id) { this.id = id; }
	
	public void setTranslation(String t) { this.translation = t; }
	
	public void setDefinition(String d) { this.definition = d; }
}

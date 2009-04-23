package net.sourceforge.jvlt.hibernate;

import java.util.*;

public class Dict {
	private Long id = null;
	private String language = null;
	private Set<Entry> entries = new HashSet<Entry>();
	private Set<Example> examples = new HashSet<Example>();
	
	public Long getId() { return this.id; }
	
	public String getLanguage() { return this.language; }
	
	public Set<Entry> getEntries() { return this.entries; }
	
	public Set<Example> getExamples() { return this.examples; }

	public void setId(Long id) { this.id = id; }
	
	public void setLanguage(String l) { this.language = l; }
	
	public void setEntries(Set<Entry> s) { this.entries = s; }
	
	public void setExamples(Set<Example> s) { this.examples = s; }
}

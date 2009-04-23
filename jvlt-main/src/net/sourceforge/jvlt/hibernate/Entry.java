package net.sourceforge.jvlt.hibernate;

import java.util.*;

public class Entry {
	private Long id = null;
	private String lesson = null;
	private String orthography = null;
	private EntryClass entryClass = null;
	private EntryStats stats = null;
	private List<String> categories = new ArrayList<String>();
	private List<String> pronunciations = new ArrayList<String>();
	private List<String> multimediaFiles = new ArrayList<String>();
	private List<Sense> senses = new ArrayList<Sense>();
	
	public Long getId() { return this.id; }
	
	public String getLesson() { return this.lesson; }
	
	public String getOrthography() { return this.orthography; }
	
	public EntryClass getEntryClass() { return this.entryClass; }
	
	public EntryStats getStats() { return this.stats; }
	
	public List<String> getCategories() { return this.categories; }
	
	public List<String> getPronunciations() { return this.pronunciations; }
	
	public List<String> getMultimediaFiles() { return this.multimediaFiles; }
	
	public List<Sense> getSenses() { return this.senses; }

	public void setId(Long id) { this.id = id; }
	
	public void setLesson(String l) { this.lesson = l; }

	public void setOrthography(String o) { this.orthography = o; }
	
	public void setEntryClass(EntryClass e) { this.entryClass = e; }
	
	public void setStats(EntryStats s) { this.stats = s; }
	
	public void setCategories(List<String> c) { this.categories = c; }
	
	public void setPronunciations(List<String> p) { this.pronunciations = p; }
	
	public void setMultimediaFiles(List<String> m) { this.multimediaFiles = m; }
	
	public void setSenses(List<Sense> s) { this.senses = s; }
}

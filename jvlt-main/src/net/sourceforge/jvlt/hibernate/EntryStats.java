package net.sourceforge.jvlt.hibernate;

import java.util.Date;

public class EntryStats {
	private Long id = null;
	private int batch = 0;
	private int numQuizzed = 0;
	private int numMistakes = 0;
	private Date lastQuizzed = null;
	private Date dateAdded = null;
	
	public Long getId() { return this.id; }
	
	public int getBatch() { return this.batch; }
	
	public int getNumQuizzed() { return this.numQuizzed; }
	
	public int getNumMistakes() { return this.numMistakes; }
	
	public Date getLastQuizzed() { return this.lastQuizzed; }
	
	public Date getDateAdded() { return this.dateAdded; }
	
	public void setId(Long id) { this.id = id; }
	
	public void setBatch(int b) { this.batch = b; }

	public void setNumQuizzed(int n) { this.numQuizzed = n; }

	public void setNumMistakes(int n) { this.numMistakes = n; }
	
	public void setLastQuizzed(Date d) { this.lastQuizzed = d; }

	public void setDateAdded(Date d) { this.dateAdded = d; }
}

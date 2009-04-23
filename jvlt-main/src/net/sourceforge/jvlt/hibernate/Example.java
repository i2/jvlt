package net.sourceforge.jvlt.hibernate;

import java.util.*;

public class Example {
	public static class TextFragment {
		private Long id = null;
		private String text = null;
		private Sense sense = null;
		
		public TextFragment() {}
		
		public Long getId() { return this.id; }
		
		public String getText() { return this.text; }
		
		public Sense getSense() { return this.sense; }
		
		public void setId(Long id) { this.id = id; }
		
		public void setText(String t) { this.text = t; }
		
		public void setSense(Sense s) { this.sense = s; }
	}
	
	private Long id = null;
	private String translation = null;
	private List<TextFragment> fragments = new ArrayList<TextFragment>();
	
	public Example() {}
	
	public Long getId() { return this.id; }
	
	public String getTranslation() { return this.translation; }
	
	public List<TextFragment> getTextFragments() { return this.fragments; } 
	
	public void setId(Long id) { this.id = id; }
	
	public void setTranslation(String t) { this.translation = t; }
	
	public void setTextFragments(List<TextFragment> l ) { this.fragments = l; }
}

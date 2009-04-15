package net.sourceforge.jvlt;

public class QuizInfo {
	/* Serialized members */
	private String _name = null;
	private String _language = null;
	private String _quizzed_attribute = null;
	private String[] _shown_attributes = new String[0];
	
	public String getName() { return _name; }
	
	public void setName(String name) { _name = name; }
	
	public String getLanguage() { return _language; }

	public void setLanguage(String language) { _language = language; }

	public String getQuizzedAttribute() { return _quizzed_attribute; }
	
	public void setQuizzedAttribute(String attribute) {
		_quizzed_attribute = attribute;
	}
	
	public String[] getShownAttributes() { return _shown_attributes; }
	
	public void setShownAttributes(String[] attributes) {
		_shown_attributes = attributes;
	}
	
	public int hashCode() { return _name.hashCode(); }

	public static QuizInfo getDefaultQuizInfo() {
		QuizInfo info = new QuizInfo();
		info.setName(GUIUtils.getString("Labels", "default"));
		info.setLanguage(null);
		info.setQuizzedAttribute("Orthography");
		info.setShownAttributes(new String[] { "Senses" });
		
		return info;
	}
}


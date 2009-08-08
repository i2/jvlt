package net.sourceforge.jvlt;

public class QuizInfo {
	private static QuizInfo[] _default_quiz_infos;
	
	static {
		_default_quiz_infos = new QuizInfo[2];
		
		_default_quiz_infos[0] = new QuizInfo();
		_default_quiz_infos[0].setName(
				GUIUtils.getString("Labels", "original"));
		_default_quiz_infos[0].setQuizzedAttribute("Orthography");
		_default_quiz_infos[0].setShownAttributes(
				new String[] { "Senses" });
		
		_default_quiz_infos[1] = new QuizInfo();
		_default_quiz_infos[1].setName(
				GUIUtils.getString("Labels", "meanings"));
		_default_quiz_infos[1].setQuizzedAttribute("Senses");
		_default_quiz_infos[1].setShownAttributes(
				new String[] { "Orthography" });
	}
	
	public static QuizInfo[] getDefaultQuizInfos() {
		return _default_quiz_infos;
	}

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
}


package net.sourceforge.jvlt;

import javax.xml.xpath.XPathExpressionException;

public class QuizInfo {
	private String _name = null;
	private String _language = null;
	private Attribute _quizzed_attribute = null;
	private Attribute[] _shown_attributes = new Attribute[0];
	
	public String getName() { return _name; }
	
	public void setName(String name) { _name = name; }
	
	public String getLanguage() { return _language; }

	public void setLanguage(String language) { _language = language; }

	public Attribute getQuizzedAttribute() { return _quizzed_attribute; }
	
	public void setQuizzedAttribute(Attribute attribute) {
		_quizzed_attribute = attribute;
	}
	
	public Attribute[] getShownAttributes() { return _shown_attributes; }
	
	public void setShownAttributes(Attribute[] attributes) {
		_shown_attributes = attributes;
	}
	
	public int hashCode() { return _name.hashCode(); }

	private EntryMetaData getMetaData(String language) {
		EntryMetaData data = new EntryMetaData();
		if (language != null) {
			EntryAttributeSchemaReader r = new EntryAttributeSchemaReader();
			try {
				EntryAttributeSchema s = r.readSchema(language);
				data.setAttributeSchema(s);
			} catch (XPathExpressionException ex) {}
		}

		return data;
	}
	
	public static QuizInfo getDefaultQuizInfo() {
		QuizInfo info = new QuizInfo();
		info.setName(GUIUtils.getString("Labels", "default"));
		info.setLanguage(null);

		EntryMetaData data = info.getMetaData(null); 
		info.setQuizzedAttribute(data.getAttribute("Orthography"));
		info.setShownAttributes(
			new Attribute[] { data.getAttribute("Senses") });
		
		return info;
	}
}


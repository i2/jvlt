package net.sourceforge.jvlt;

import javax.xml.xpath.XPathExpressionException;

public class QuizInfo implements StringSerializable {
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
	
	public String convertToString() {
		String[] array = new String[_shown_attributes.length];
		for (int i=0; i<_shown_attributes.length; i++)
			array[i] = Utils.escapeString(_shown_attributes[i].getName(), "{}");
		String shown_attributes_string=StringSerializableUtils.join(array);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName());
		buffer.append("{");
		buffer.append(StringSerializableUtils.join(new String[] {
			Utils.escapeString(_name, "{}"),
			_language==null ? "" : _language,
			Utils.escapeString(_quizzed_attribute.getName(), "{}"),
			"{" + shown_attributes_string + "}" }));
		buffer.append("}");
		return buffer.toString();
	}

	public void initFromString(String str)
		throws DeserializationException {
		String[] attributes = StringSerializableUtils.split(str);
		if (attributes.length < 4)
			throw new DeserializationException("Not enough attributes.");
		
		_name = Utils.unescapeString(attributes[0], "{}");

		String lang_str = attributes[1];
		_language = lang_str.equals("") ? null : lang_str;
		EntryMetaData data = getMetaData(_language);
		
		_quizzed_attribute = data.getAttribute(
			Utils.unescapeString(attributes[2], "{}"));
		if (_quizzed_attribute == null)
			throw new DeserializationException(
				"Warning: Attribute \""+attributes[2]+"\" does not exist.");
		
		String[] array = StringSerializableUtils.split(
			attributes[3].substring(1, attributes[3].length()-1));
		_shown_attributes = new Attribute[array.length];
		for (int i=0; i<array.length; i++) {
			_shown_attributes[i] = data.getAttribute(
				Utils.unescapeString(array[i], "{}"));
			if (_shown_attributes[i] == null)
				throw new DeserializationException(
					"Warning: Attribute \""+array[i]+"\" does not exist.");
		}
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


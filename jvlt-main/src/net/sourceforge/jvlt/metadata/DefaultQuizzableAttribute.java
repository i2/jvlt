package net.sourceforge.jvlt.metadata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultQuizzableAttribute implements QuizzableAttribute {
	private Attribute _attribute;
	
	public DefaultQuizzableAttribute(Attribute attribute) {
		_attribute = attribute;
	}
	
	@Override
	public String getName() {
		return _attribute.getName();
	}

	@Override
	public Class<? extends Object> getType() {
		return _attribute.getType();
	}

	@Override
	public Object getValue(Object o) {
		return _attribute.getValue(o);
	}

	@Override
	public String getFormattedValue(Object o) {
		return _attribute.getFormattedValue(o);
	}

	@Override
	public Element getXMLElement(Document doc, Object o) {
		return _attribute.getXMLElement(doc, o);
	}

	@Override
	public boolean matches(Object o, String input, boolean matchCase) {
		String normalizedInput = input.trim();
		if (matchCase) {
			normalizedInput = normalizedInput.toLowerCase();
		}
		
		String solution = getFormattedValue(o).trim();
		if (matchCase) {
			solution = solution.toLowerCase();
		}
		
		return solution.equals(normalizedInput);
	}

}

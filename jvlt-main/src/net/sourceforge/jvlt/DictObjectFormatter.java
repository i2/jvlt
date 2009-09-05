package net.sourceforge.jvlt;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DictObjectFormatter {
	private Document _doc;
	
	public DictObjectFormatter(Document doc) { _doc = doc; }
	
	public Element getElementForObject(Object obj, Attribute[] attributes) {
		String name = obj.getClass().getSimpleName();
		Element elem = _doc.createElement(name);
		for (int i=0; i<attributes.length; i++) {
			Attribute attr = attributes[i];
			if (attr.getValue(obj) != null)
				elem.appendChild(attr.getXMLElement(_doc, obj));
		}
		return elem;
	}
}


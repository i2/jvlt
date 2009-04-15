package net.sourceforge.jvlt;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CustomAttribute extends DefaultAttribute {
	public CustomAttribute(String name) { super(name, String.class); }
	
	public Object getValue(Object obj) {
		EntryClass ec = ((Entry) obj).getEntryClass();
		if (ec == null)
			return null;
		else {
			SchemaAttribute attr = ec.getAttribute(_name);
			if (attr == null)
				return null;
			else
				return attr.getValue();
		}
	}
	
	public Element getXMLElement(Document doc, Object o) {
		Element elem = doc.createElement("Attribute");
		elem.setAttribute("name", _name);
		elem.appendChild(doc.createTextNode(getFormattedValue(o)));
		return elem;
	}
}


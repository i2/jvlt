package net.sourceforge.jvlt.metadata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Attribute {
	public String getName();

	public Class<? extends Object> getType();

	public Object getValue(Object o);

	public String getFormattedValue(Object o);

	public Element getXMLElement(Document doc, Object o);
}

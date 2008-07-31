package net.sourceforge.jvlt;

import java.lang.reflect.Method;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultAttribute
		implements Attribute, Comparable<DefaultAttribute> {
	protected String _name;
	protected Class<? extends Object> _type;

	public DefaultAttribute (String name, Class<? extends Object> type) {
		_name = name;
		_type = type;
	}
	
	public String getName() { return _name; }

	public Class<? extends Object> getType() { return _type; }

	public Object getValue(Object o) { return getValue(o, _name); }
	
	public String getFormattedValue(Object o) {
		Object val = getValue(o);
		if (val == null)
			return "";
		else
			return val.toString();
	}
	
	public Element getXMLElement(Document doc, Object o) {
		return XMLUtils.createTextElement(doc, _name, getFormattedValue(o));
	}
	
	public String toString() { return _name; }

	public int compareTo(DefaultAttribute o) {
		return _name.compareTo(((DefaultAttribute) o)._name);
	}

	public boolean equals(Object o) {
		return _name.equals(((DefaultAttribute) o)._name);
	}

	protected Object getValue(Object o, String name) {
		try {
			Method method = o.getClass().getMethod("get"+name, new Class[0]);
			return method.invoke(o, new Object[0]);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}

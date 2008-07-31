package net.sourceforge.jvlt;

public class CustomArrayAttribute extends CustomChoiceAttribute {
	public CustomArrayAttribute(String name) { super(name); }
	
	public String getFormattedValue(Object o) {
		AttributeChoice[] values = (AttributeChoice[]) getValue(o);
		if (values == null)
			return "";
		
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<values.length; i++) {
			if (i>0)
				buf.append(", ");
			buf.append(_resources.getString(values[i].getName()));
		}
		return buf.toString();
	}
}


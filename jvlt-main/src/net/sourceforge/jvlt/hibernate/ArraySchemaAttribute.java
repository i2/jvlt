package net.sourceforge.jvlt.hibernate;

import java.util.*;

public class ArraySchemaAttribute extends SchemaAttribute {
	private List<String> values = new ArrayList<String>();
	
	public List<String> getValues() { return this.values; }
	
	public void setValues(List<String> v) { this.values = v; }
}

package net.sourceforge.jvlt;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;

public abstract class ObjectQueryItem implements StringSerializable {
	protected String _name;
	protected int _type;
	protected Object _value;
	
	public ObjectQueryItem(String name, int type, Object value) {
		_name = name;
		_type = type;
		_value = value;
	}
	
	public ObjectQueryItem() { this(null, 0, null); }
	
	public String getName() { return _name; }
	
	public void setName(String name) { _name = name; }
	
	public int getType() { return _type; }
	
	public void setType(int type) { _type = type; }
	
	public Object getValue() { return _value; }
	
	public void setValue(Object value) { _value = value; }

	public int[] getTypes() {
		Field[] fields = getTypeFields();
		int[] types = new int[fields.length];
		for (int i=0; i<fields.length; i++) {
			try { types[i] = fields[i].getInt(null); }
			catch (IllegalAccessException ex) { ex.printStackTrace(); }
		}
		
		return types;
	}
	
	public String[] getTypeNames() {
		Field[] fields = getTypeFields();
		String[] names = new String[fields.length];
		for (int i=0; i<fields.length; i++)
			names[i] = fields[i].getName();
		
		return names;
	}
	
	public String convertToString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getClass().getName());
		buf.append("{");
		buf.append(StringSerializableUtils.join(new String[] {
			_name, String.valueOf(_type), convertToString(_value) }));
		buf.append("}");
		return buf.toString();
	}

	public void initFromString(String value) throws DeserializationException {
		String[] attributes = StringSerializableUtils.split(value);
		if (attributes.length < 3)
			throw new DeserializationException("Not enough attributes");
		
		_name = attributes[0];
		_type = Integer.parseInt(attributes[1]);
		_value = createFromString(attributes[2]);
	}
	
	public abstract boolean objectMatches(Object o);

	protected Field[] getTypeFields() {
		Field[] fields = this.getClass().getFields();
		Vector<Field> field_vector = new Vector<Field>();
		for (int i=0; i<fields.length; i++) {
			Field field = fields[i];
			int modifiers = field.getModifiers();
			if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers)
				&& Modifier.isStatic(modifiers)
				&& field.getType().getName().equals("int"))
				field_vector.add(field);
		}
		
		return (Field[]) field_vector.toArray(new Field[0]);
	}
	
	protected String convertToString(Object value) {
		return StringSerializableUtils.convertToString(value);
	}
		
	protected Object createFromString(String value)
		throws DeserializationException {
		return StringSerializableUtils.createFromString(value);
	}
}


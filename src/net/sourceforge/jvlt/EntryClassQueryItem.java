package net.sourceforge.jvlt;

public class EntryClassQueryItem extends ObjectQueryItem {
	public static final int EQUALS = 0;
	public static final int NOT_EQUAL = 1;
	
	public EntryClassQueryItem(String name, int type, Object value) {
		super(name, type, value); }
		
	public EntryClassQueryItem() { super(); }
	
	public boolean objectMatches(Object obj) {
		if (_type == EntryClassQueryItem.EQUALS)
			return _value==null ? obj==null : _value.equals(obj);
		else if (_type == EntryClassQueryItem.NOT_EQUAL)
			return ! (_value==null ? obj==null : _value.equals(obj));
		else
			return false;
	}

	protected String convertToString(Object value) {
		if (value == null)
			return "";
		else
			return ((EntryClass) value).getName();
	}
		
	protected Object createFromString(String value) {
		return new EntryClass(value); }
}


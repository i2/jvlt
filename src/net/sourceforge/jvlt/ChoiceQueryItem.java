package net.sourceforge.jvlt;

public class ChoiceQueryItem extends ObjectQueryItem {
	public static final int EQUALS = 0;
	public static final int NOT_EQUAL = 1;
	public static final int CONTAINS = 2;
	
	public ChoiceQueryItem(String name, int type, Object value) {
		super(name, type, value);
	}
	
	public ChoiceQueryItem() { this("", EQUALS, null); }
	
	public boolean objectMatches(Object obj) {
		if (obj==null || _value==null)
			return obj==null && _value==null;
		
		if (_type == StringQueryItem.EQUALS)
			return obj.equals(_value);
		else if (_type == NOT_EQUAL)
			return ! obj.equals(_value);
		else if (_type == CONTAINS)
			return obj.toString().toLowerCase().indexOf(
					_value.toString().toLowerCase()) >= 0;
		else
			return false;
	}
}

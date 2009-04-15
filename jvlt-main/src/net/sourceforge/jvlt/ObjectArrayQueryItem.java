package net.sourceforge.jvlt;

public class ObjectArrayQueryItem extends ObjectQueryItem {
	public static final int ITEM_CONTAINS = 0;
	public static final int EMPTY = 1;
	public static final int NOT_EMPTY = 2;

	private boolean _match_case = true;
	
	public ObjectArrayQueryItem (String name, int type, Object value) {
		super(name, type, value);
	}
	
	public ObjectArrayQueryItem () { this(null, 0, null); }

	public boolean getMatchCase() { return _match_case; }

	public void setMatchCase(boolean match) { _match_case = match; }

	public boolean objectMatches(Object obj) {
		Object[] array = (obj == null) ? new Object[0] : (Object[]) obj;
		
		if (_type == EMPTY)
			return array.length == 0;
		else if (_type == NOT_EMPTY)
			return array.length > 0;
		else if (_type == ITEM_CONTAINS) {
			if (_value == null)
				return true;
			for (int i=0; i<array.length; i++)
				if (_match_case) {
					if (array[i].toString().indexOf(_value.toString()) >= 0)
						return true;
				} else {
					if (array[i].toString().toLowerCase().indexOf(
						_value.toString().toLowerCase()) >= 0)
						return true;
				}
			
			return false;
		} else
			return false;
	}
}


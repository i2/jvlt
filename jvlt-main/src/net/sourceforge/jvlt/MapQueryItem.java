package net.sourceforge.jvlt;

import java.util.Map;

public class MapQueryItem extends ObjectQueryItem {
	public static final int KEY_CONTAINS = 0;
	public static final int VALUE_CONTAINS = 1;
	
	private boolean _match_case = false;
	
	public MapQueryItem(String name, int type, Object value) {
		super(name, type, value);
	}

	public MapQueryItem() { super(); }

	public boolean getMatchCase() { return _match_case; }

	public void setMatchCase(boolean match) { _match_case = match; }

	@Override
	@SuppressWarnings(value = "unchecked")
	public boolean objectMatches(Object o) {
		String text = _value != null ? (String) _value : "";
		Map<String, String> map = (Map<String, String>) o;
		
		switch (_type) {
		case KEY_CONTAINS:
			for (String s: map.keySet())
				if (_match_case) {
					if (s.indexOf(text) >= 0)
						return true;
				} else {
					if (s.toLowerCase().indexOf(text.toLowerCase()) >= 0)
						return true;
					
				}
			
			return text.length() == 0;
		case VALUE_CONTAINS:
			for (String s: map.values())
				if (_match_case) {
					if (s.indexOf(text) >= 0)
						return true;
				} else {
					if (s.toLowerCase().indexOf(text.toLowerCase()) >= 0)
						return true;
					
				}
			
			return text.length() == 0;
		default:
			return false;
		}
	}
}

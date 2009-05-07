package net.sourceforge.jvlt;

public class SenseArrayQueryItem extends ObjectQueryItem {
	public static final int TRANSLATION_CONTAINS = 1;
	public static final int TRANSLATION_EQUALS = 2;
	public static final int DEFINITION_CONTAINS = 3;
	public static final int DEFINITION_EQUALS = 4;

	private boolean _match_case = false;

	public SenseArrayQueryItem(int type, Object value) {
		super("Senses", type, value);
	}
	
	public SenseArrayQueryItem() { this(1, null); }

	public boolean getMatchCase() { return _match_case; }

	public void setMatchCase(boolean match) { _match_case = match; }

	public boolean objectMatches(Object obj) {
		Sense[] senses = (Sense[]) obj;
		String val = _match_case ?
			_value.toString() : _value.toString().toLowerCase();
		for (int i=0; i<senses.length; i++) {
			String str;
			if (_type == SenseArrayQueryItem.TRANSLATION_CONTAINS
				|| _type == SenseArrayQueryItem.TRANSLATION_EQUALS)
				str = _match_case ?  senses[i].getTranslation() :
					senses[i].getTranslation().toLowerCase();
			else
				str = _match_case ?  senses[i].getTranslation() :
					senses[i].getDefinition().toLowerCase();

			if (_type == SenseArrayQueryItem.TRANSLATION_CONTAINS
				|| _type == SenseArrayQueryItem.DEFINITION_CONTAINS) {
				if (str.indexOf(val)>=0)
					return true;
			} else if (_type == SenseArrayQueryItem.TRANSLATION_EQUALS
				|| _type == SenseArrayQueryItem.DEFINITION_EQUALS) {
				if (str.equals(val))
					return true;
			}
		}
		return false;
	}
}


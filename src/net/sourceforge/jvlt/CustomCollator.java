package net.sourceforge.jvlt;

import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

public class CustomCollator extends RuleBasedCollator {
	private static CustomCollator _instance = null;
	
	private CustomCollator() throws ParseException {
		super(((RuleBasedCollator) RuleBasedCollator.getInstance(Locale.US))
				.getRules() + "< ' '");
	}
	
	public static CustomCollator getInstance() {
		try {
			if (_instance == null)
				_instance = new CustomCollator();
			
			return _instance;
		} catch (ParseException e) {
			return null; // Should not happen
		}
	}
}

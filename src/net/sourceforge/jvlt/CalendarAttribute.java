package net.sourceforge.jvlt;

import java.text.SimpleDateFormat;
import java.util.Calendar;

class CalendarAttribute extends DefaultAttribute {
	private static SimpleDateFormat _format =
		new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public CalendarAttribute(String name, Class<? extends Object> type) {
		super(name, type);
	}
		
	public String getFormattedValue(Object o) {
		Calendar val = (Calendar) getValue(o);
		if (val == null)
			return "";
		else
			return _format.format(val.getTime());
	}
}


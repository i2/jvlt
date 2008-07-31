package net.sourceforge.jvlt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StringSerializableUtils {
	public static Object createFromString(String value)
		throws DeserializationException {
		if (value.equals(""))
			return null;
		
		int pos = value.indexOf('{');
		if (pos < 0)
			throw new DeserializationException("Invalid format");
		
		String class_string = value.substring(0,pos);
		String value_string = value.substring(pos+1,value.length()-1);
		try {
			Class<? extends Object> cl = Class.forName(class_string);
			if (StringSerializable.class.isAssignableFrom(cl)) {
				StringSerializable obj = (StringSerializable) cl.newInstance();
				obj.initFromString(value_string);
				return obj;
			} else
				return createFromString(cl, value_string);
		} catch (ClassNotFoundException e) {
			throw new DeserializationException(e.getMessage());
		} catch (InstantiationException e) {
			throw new DeserializationException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new DeserializationException(e.getMessage());
		}
	}
	
	public static String convertToString(Object obj) {
		if (obj == null)
			return "";
		else if (obj instanceof StringSerializable)
			return ((StringSerializable) obj).convertToString();
		else
			return obj.getClass().getName() + "{" +
				convertToString(obj.getClass(), obj) + "}";
	}

	public static String[] split(String string) {
		// Example: Let string be "5;B{1;2};C{3}". Then an array containing the
		// three strings "5", "B{1;2}" and "C{3}" is returned.
		if (string == null || string.equals(""))
			return new String[0];
		
		ArrayList<String> list = new ArrayList<String>();
		int level = 0;
		int last_index = 0;
		for (int i=0; i<string.length(); i++) {
			char c = string.charAt(i);
			if (c == '{')
				level++;
			else if (c == '}')
				level--;
			else if (c == ';' && level == 0) {
				list.add(string.substring(last_index, i));
				last_index = i+1;
			}
		}
		list.add(string.substring(last_index, string.length()));
		return list.toArray(new String[0]);
	}
	
	public static String join(String[] strings) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<strings.length; i++) {
			if (i>0)
				buf.append(";");
			buf.append(escapeString(strings[i]));
		}
		return buf.toString();
	}

	private static String escapeString(String string) {
		StringBuffer buf = new StringBuffer();
		int level = 0;
		for (int i=0; i<string.length(); i++) {
			char c = string.charAt(i);
			String str = "" + c;
			if (c == '{')
				level++;
			else if (c == '}')
				level--;
			else if (c == ';' && level == 0)
				str = Utils.escapeChar(';');
			
			buf.append(str);
		}
		return buf.toString();
	}
	
	private static Object createFromString(Class<? extends Object> cl,
			String value) throws DeserializationException {
		if (Calendar.class.isAssignableFrom(cl)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				Date date = sdf.parse(value);
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTimeInMillis(date.getTime());
				return gc;
			} catch (ParseException e) {
				throw new DeserializationException(e.getMessage());
			}
		} else if (Number.class.isAssignableFrom(cl)) {
			return new Double(Double.parseDouble(value));
		} else if (String.class.isAssignableFrom(cl)) {
			return value;
		} else
			return null;
	}
	
	private static String convertToString(
			Class<? extends Object> cl, Object obj) {
		if (Calendar.class.isAssignableFrom(cl)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(((Calendar) obj).getTime());
		} else if (Number.class.isAssignableFrom(cl)) {
			return String.valueOf(((Number) obj).doubleValue());
		} else if (String.class.isAssignableFrom(cl)) {
			return obj.toString();
		} else
			return null;
	}
}


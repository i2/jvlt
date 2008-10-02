package net.sourceforge.jvlt;

import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
	public static String removeSubstring(
		String s, int begin_index, int end_index) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<s.length(); i++)
			if (i<begin_index || i>end_index)
				buf.append(s.charAt(i));
				
		return buf.toString();
	}
	
	/**
	 * Insert a String into an other.
	 * @param s The string into which the other string is inserted. <i>s</i>
	 *   will not be modified itself - instead, the result will be returned
	 *   by the function.
	 * @param index The index of the character before which the
	 *   the string will be inserted. If index is equal to the length of
	 *   <i>s</i> then the string will be appended.
	 * @param t The string to be inserted.
	 */
	public static String insertString(String s, int index, String t) {
		String before;
		if (index==0)
			before="";
		else
			before = s.substring(0, index);
		String after;
		if (index==s.length())
			after="";
		else
			after = s.substring(index, s.length());
			
		return before+t+after;
	}
	
	public static void removeClassInstances(
			Collection<? extends Object> c, String class_name) {
		try {
			Class<? extends Object> cl = Class.forName(class_name);
			Iterator<? extends Object> it = c.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (cl.isInstance(obj))
					it.remove();
			}
		}
		catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static boolean arrayContainsItem(Object[] array, Object item) {
		for (int i=0; i<array.length; i++)
			if (array[i].equals(item))
				return true;

		return false;
	}

	public static URL getDirectory(URL url) {
		try {
			String p = url.getPath().substring(
				0, url.getPath().lastIndexOf("/") + 1);
			URL d = new URL(url.getProtocol(), url.getHost(),
				url.getPort(), p);
			return d;
		}
		catch (MalformedURLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static String fontToString(Font font) {
		int style = font.getStyle();
		String style_str;
		switch (style) {
			case Font.PLAIN:
				style_str="PLAIN"; break;
			case Font.BOLD:
				style_str="BOLD"; break;
			case Font.ITALIC:
				style_str="ITALIC"; break;
			case Font.BOLD | Font.ITALIC:
				style_str="BOLDITALIC"; break;
			default:
				throw new RuntimeException("Invalid style:"+style);
		}
		
		return font.getFamily()+"-"+style_str+"-"+font.getSize();
	}

	public static String arrayToString(Object[] values, String delim) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<values.length; i++) {
			if (i>0)
				buf.append(delim);
			buf.append(values[i].toString());
		}
		
		return buf.toString();
	}
	
	public static String arrayToString(Object[] values) {
		return arrayToString(values, ";");
	}
	
	public static String[] split(String str) { return split(str, ";"); }
	
	/**
	 * Split a single string into multiple strings.
	 * @return An empty array if argument str is the empty string, otherwise
	 *   the same as {@link String#split(String)} yields. */
	public static String[] split(String str, String delim) {
		if (str == null || str.equals(""))
			return new String[0];
		else
			return str.split(delim);
	}
	
	public static String calendarToString(Calendar date) {
		if (date == null)
			return "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date.getTime());
	}
	
	public static String[] objectArrayToStringArray(Object[] values) {
		String[] array = new String[values.length];
		for (int i=0; i<array.length; i++)
			array[i] = values[i].toString();
		
		return array;
	}
	
	public static boolean arraysEqual(Object[] array1, Object[] array2) {
		if (array1 == null)
			return array2 == null;
		if (array1.length != array2.length)
			return false;
		
		for (int i=0; i<array1.length; i++)
			if (! array1[i].equals(array2[i]))
				return false;
		
		return true;
	}
	
	/**
	 * Escape a character.
	 * The escape sequence consists of the prefix /u and 4 digits that
	 * represent the hex-code for the character. */
	public static String escapeChar(char ch) {
		String hex = Integer.toHexString((int) ch);
		if (hex.length() == 1)
			return "/u000" + hex;
		else if (hex.length() == 2)
			return "/u00" + hex;
		else if (hex.length() == 3)
			return "/u0" + hex;
		else
			return "/u" + hex;
	}
}


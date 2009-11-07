package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.Font;
import java.util.*;

public class Config extends Properties {
	private static final long serialVersionUID = 1L;
	
	public float getFloatProperty (String key, float default_val) {
		String str = getProperty(key, String.valueOf(default_val));
		float val;
		try {
			val = Float.parseFloat(str);
		}
		catch (NumberFormatException ex) {
			val = default_val;
		}
		
		return val;
	}

	public int getIntProperty (String key, int default_val) {
		String str = getProperty(key, String.valueOf(default_val));
		int val;
		try { val = Integer.parseInt(str); }
		catch (NumberFormatException e) { val = default_val; }
		return val;
	}
	
	public boolean getBooleanProperty(String key, boolean default_val) {
		String str = getProperty(key);
		if (str == null)
			return default_val;
		else if (str.equals("true"))
			return true;
		else if (str.equals("false"))
			return false;
		else
			return default_val;
	}

	public Font getFontProperty(String key) {
		String str = getProperty(key);
		
		if (str == null || str.equals(""))
			return null;
		else
			return Font.decode(str);
	}
	
	public String[] getStringListProperty(String key, String[] def)	{
		String prop = getProperty(key, Utils.arrayToString(def));
		return Utils.split(prop, ";");
	}
	
	public double[] getNumberListProperty(String key, double[] def)	{
		String[] defstr = new String[def.length];
		for (int i=0; i<def.length; i++)
			defstr[i] = String.valueOf(def[i]);
		
		String[] strings = getStringListProperty(key, defstr);
		double[] values = new double[strings.length];
		for (int i=0; i<strings.length; i++)
			values[i] = Double.parseDouble(strings[i]);
		
		return values;
	}
	
	public Locale getLocaleProperty(String key, Locale def)	{
		Locale loc = def;
		String str = getProperty(key, def.toString());
		int indexof = str.indexOf("_");
		if (indexof < 0)
			loc = new Locale(str);
		else {
			String lang = str.substring(0, indexof);
			String country = str.substring(indexof+1, str.length());
			loc = new Locale(lang, country);
		}
		
		return loc;
	}
	
	public Dimension getDimensionProperty(String key, Dimension default_dim) {
		String val;
		if (default_dim != null)
			val = getProperty(key, String.valueOf(default_dim.width) + ";"
					+ String.valueOf(default_dim.height));
		else
			val = getProperty(key);
		
		try {
			String[] size = val.split(";");
			if (size.length != 2)
				return null;
			
			return new Dimension(Integer.parseInt(size[0]),
					Integer.parseInt(size[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public void setProperty(String key, boolean value) {
		put(key, String.valueOf(value));
	}
		
	public void setProperty(String key, int value) {
		put(key, String.valueOf(value));
	}

	public void setProperty(String key, float value) {
		put(key, String.valueOf(value));
	}

	public void setProperty(String key, Font value)	{
		put(key, Utils.fontToString(value));
	}
	
	public void setProperty(String key, Object[] values) {
		String str = Utils.arrayToString(values);
		put(key, str);
	}

	public void setProperty(String key, Locale value) {
		put(key, value.toString());
	}
	
	public void setProperty(String key, Dimension dim) {
		put(key, Utils.arrayToString(new String[] {
				String.valueOf(dim.width), String.valueOf(dim.height)}, ";"));
	}
}


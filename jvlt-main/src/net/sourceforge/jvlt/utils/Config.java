package net.sourceforge.jvlt.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Config {
	private final TreeMap<String, String> properties = new TreeMap<String, String>();

	public String[] getKeys() {
		return properties.keySet().toArray(new String[0]);
	}

	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

	public String getProperty(String key) {
		return getProperty(key, null);
	}

	public String getProperty(String key, String default_value) {
		if (properties.containsKey(key)) {
			return properties.get(key);
		}
		return default_value;
	}

	public float getFloatProperty(String key, float default_val) {
		String str = getProperty(key, String.valueOf(default_val));
		float val;
		try {
			val = Float.parseFloat(str);
		} catch (NumberFormatException ex) {
			val = default_val;
		}

		return val;
	}

	public int getIntProperty(String key, int default_val) {
		String str = getProperty(key, String.valueOf(default_val));
		int val;
		try {
			val = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			val = default_val;
		}
		return val;
	}

	public boolean getBooleanProperty(String key, boolean default_val) {
		String str = getProperty(key);
		if (str == null) {
			return default_val;
		} else if (str.equals("true")) {
			return true;
		} else if (str.equals("false")) {
			return false;
		} else {
			return default_val;
		}
	}

	public Font getFontProperty(String key) {
		String str = getProperty(key);

		if (str == null || str.equals("")) {
			return null;
		}
		return Font.decode(str);
	}

	public String[] getStringListProperty(String key, String[] def) {
		String prop = getProperty(key, Utils.arrayToString(def));
		return Utils.split(prop, ";");
	}

	public String[] getStringListProperty(String key) {
		return Utils.split(getProperty(key), ";");
	}

	public double[] getNumberListProperty(String key, double[] def) {
		String[] defstr = new String[def.length];
		for (int i = 0; i < def.length; i++) {
			defstr[i] = String.valueOf(def[i]);
		}

		String[] strings = getStringListProperty(key, defstr);
		double[] values = new double[strings.length];
		for (int i = 0; i < strings.length; i++) {
			values[i] = Double.parseDouble(strings[i]);
		}

		return values;
	}

	public Locale getLocaleProperty(String key, Locale def) {
		Locale loc = def;
		String str = getProperty(key, def.toString());
		int indexof = str.indexOf("_");
		if (indexof < 0) {
			loc = new Locale(str);
		} else {
			String lang = str.substring(0, indexof);
			String country = str.substring(indexof + 1, str.length());
			loc = new Locale(lang, country);
		}

		return loc;
	}

	public Dimension getDimensionProperty(String key, Dimension default_dim) {
		String val;
		if (default_dim != null) {
			val = getProperty(key, String.valueOf(default_dim.width) + ";"
					+ String.valueOf(default_dim.height));
		} else {
			val = getProperty(key);
		}

		try {
			String[] size = val.split(";");
			if (size.length != 2) {
				return null;
			}

			return new Dimension(Integer.parseInt(size[0]), Integer
					.parseInt(size[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	public void setProperty(String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}

	public void setProperty(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	public void setProperty(String key, float value) {
		setProperty(key, String.valueOf(value));
	}

	public void setProperty(String key, Font value) {
		setProperty(key, Utils.fontToString(value));
	}

	public void setProperty(String key, Object[] values) {
		String str = Utils.arrayToString(values);
		setProperty(key, str);
	}

	public void setProperty(String key, Locale value) {
		setProperty(key, value.toString());
	}

	public void setProperty(String key, Dimension dim) {
		setProperty(key, Utils.arrayToString(new String[] {
				String.valueOf(dim.width), String.valueOf(dim.height) }, ";"));
	}

	public void remove(String key) {
		properties.remove(key);
	}

	public void load(FileInputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				if (line.startsWith("#")) {
					continue;
				}

				int pos = line.indexOf('=');
				if (pos < 0) {
					continue;
				}

				String key = line.substring(0, pos);
				String value = line.substring(pos + 1, line.length());
				properties.put(key, value);
			}
		} finally {
			reader.close();
		}
	}

	public void store(FileOutputStream out) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,
				"UTF-8"));
		try {
			writer.println("#jVLT property file");
			writer.println("#"
					+ DateFormat.getDateTimeInstance().format(new Date()));
			for (Map.Entry<String, String> e : properties.entrySet()) {
				writer.println(e.getKey() + "=" + e.getValue());
			}
		} finally {
			writer.flush();
			writer.close();
		}
	}
}

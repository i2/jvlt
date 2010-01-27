package net.sourceforge.jvlt.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;

public class AttributeResources extends ResourceBundle {
	private HashSet<String> _keys;
	private ResourceBundle _bundle;

	public AttributeResources() {
		_keys = new HashSet<String>();
		_bundle = ResourceBundle.getBundle("i18n/Attributes", Locale
				.getDefault());
		for (Enumeration<String> e = _bundle.getKeys(); e.hasMoreElements();)
			_keys.add(e.nextElement());
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(_keys);
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key == null || key.equals(""))
			return key;
		else if (_keys.contains(key))
			return _bundle.getObject(key);
		else
			return key;
	}
}

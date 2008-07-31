package net.sourceforge.jvlt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class PropertyMap {
	private Vector<PropertyChangeListener> _listeners;
	private HashMap<String, Object> _value_map;
	
	public PropertyMap() {
		_listeners = new Vector<PropertyChangeListener>();
		_value_map = new HashMap<String, Object>();
	}
	
	public boolean containsKey(String key) {
		return _value_map.containsKey(key);
	}
	
	public Object get(String key) { return _value_map.get(key); }
	
	public void put(String key, Object value) {
		boolean fire = ! _value_map.containsKey(key) ||
			! _value_map.get(key).equals(value);
		_value_map.put(key, value);
		if (fire)
			firePropertyChangeEvent(key, _value_map.get(key), value);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		_listeners.add(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		_listeners.remove(l);
	}
		
	private void firePropertyChangeEvent(String key,
		Object old_value, Object new_value) {
		for (Iterator<PropertyChangeListener> it=_listeners.iterator();
				it.hasNext(); )
			it.next().propertyChange(
				new PropertyChangeEvent(this, key, old_value, new_value));
	}
}


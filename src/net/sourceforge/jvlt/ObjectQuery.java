package net.sourceforge.jvlt;

import java.util.*;

public class ObjectQuery implements StringSerializable {
	public static final int MATCH_ONE = 0;
	public static final int MATCH_ALL = 1;
	
	private int _type;
	private String _name;
	private ArrayList<ObjectQueryItem> _items;
	private MetaData _metadata;
	
	public ObjectQuery(Class<? extends Object> cl, int type) {
		_type = type;
		_name = "";
		_items = new ArrayList<ObjectQueryItem>();
		DictModel model = JVLT.getInstance().getModel().getDictModel();
		_metadata = cl==null ? null : model.getMetaData(cl);
	}
	
	public ObjectQuery(Class<? extends Object> cl) { this(cl, MATCH_ALL); }
	
	public ObjectQuery() { this(null, MATCH_ALL); }
	
	public int getType() {	return _type; }
	
	public void setType(int type) { _type = type; }
	
	public void addItem(ObjectQueryItem item) { _items.add(item); }
	
	public ObjectQueryItem[] getItems() {
		return _items.toArray(new ObjectQueryItem[0]);
	}
		
	public String getName() { return _name; }
	
	public void setName(String name) { _name = name; }
	
	public boolean objectMatches(Object obj) {
		if (_items.size() == 0)
			return true;

		Iterator<ObjectQueryItem> it = _items.iterator();
		while (it.hasNext()) {
			ObjectQueryItem item = it.next();
			Attribute attr = _metadata.getAttribute(item.getName());
			Object value = attr.getValue(obj);
			if (item.objectMatches(value) && _type==MATCH_ONE)
				return true;
			else if (! item.objectMatches(value) && _type==MATCH_ALL)
				return false;
		}
		return _type == MATCH_ALL;
	}
	
	public String convertToString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getClass().getName());
		buf.append("{");
		String[] strings = new String[_items.size()+3];
		strings[0] = String.valueOf(_type);
		strings[1] = Utils.escapeString(_name, "{}");
		if (_metadata == null)
			strings[2] = "";
		else
			strings[2] = _metadata.getType().getName();
		
		int i=3;
		for (Iterator<ObjectQueryItem> it=_items.iterator(); it.hasNext(); i++)
			strings[i] = it.next().convertToString();
		buf.append(StringSerializableUtils.join(strings));
		buf.append("}");
		
		return buf.toString();
	}

	public void initFromString(String value)
		throws DeserializationException {
		String[] attributes = StringSerializableUtils.split(value);
		if (attributes.length < 3)
			throw new DeserializationException("Not enough attributes.");
		
		_type = Integer.parseInt(attributes[0]);
		_name = Utils.unescapeString(attributes[1], "{}");
		try {
			if (attributes[2].equals(""))
				_metadata = null;
			else {
				Class<? extends Object> cl = Class.forName(attributes[2]);
				DictModel model = JVLT.getInstance().getModel().getDictModel();
				_metadata = model.getMetaData(cl);
			}
		}
		catch (Exception e) {
			// e.printStackTrace();
			throw new DeserializationException(e.getMessage());
		}
		
		for (int i=3; i<attributes.length; i++) {
			ObjectQueryItem item = (ObjectQueryItem)
				StringSerializableUtils.createFromString(attributes[i]);
			if (item != null)
				addItem(item);
		}
	}
}


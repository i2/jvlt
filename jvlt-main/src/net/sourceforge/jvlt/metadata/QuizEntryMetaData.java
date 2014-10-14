package net.sourceforge.jvlt.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class providing access to attributes that offer extra functionality for
 * quizzes
 */
public class QuizEntryMetaData extends EntryMetaData {
	private Map<String, DefaultQuizzableAttribute> _default_attributes =
			new HashMap<String, DefaultQuizzableAttribute>();
	private Map<String, QuizzableAttribute> _special_attributes =
			new HashMap<String, QuizzableAttribute>();
	
	public QuizEntryMetaData() {
		super();
		
		_special_attributes.put("Senses", new QuizzableSensesAttribute());
	}
	
	@Override
	public Attribute getAttribute(String name) {
		if (_special_attributes.containsKey(name)) {
			// Check special attributes first
			return _special_attributes.get(name);
		} else if (!_default_attributes.containsKey(name)) {
			DefaultQuizzableAttribute qa = new DefaultQuizzableAttribute(
					super.getAttribute(name));
			_default_attributes.put(name, qa);
			return qa;
		} else {
			return _default_attributes.get(name);
		}
	}

	@Override
	public Attribute[] getAttributes() {
		List<Attribute> attrs = new ArrayList<Attribute>();
		for (String s : getAttributeNames()) {
			attrs.add(getAttribute(s));
		}
		return attrs.toArray(new Attribute[0]);
	}
}

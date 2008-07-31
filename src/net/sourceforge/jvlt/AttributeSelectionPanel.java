package net.sourceforge.jvlt;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.DefaultListModel;

public class AttributeSelectionPanel extends ObjectSelectionPanel {
	private static final long serialVersionUID = 1L;
	
	public AttributeSelectionPanel() {
		super();
		setTranslateItems(true);
		setAllowReordering(false);
	}

	protected void updateLists() {
        _selection_list_model.clear();
		addElements(_selection_list_model, _selected_objects);
		_choice_list_model.clear();
		addElements(_choice_list_model, _available_objects);
	}

	private void addElements(
			DefaultListModel model, Collection<Object> elements) {
		TreeSet<String> standard_attrs = new TreeSet<String>();
		TreeSet<String> custom_attrs = new TreeSet<String>();
        for (Iterator<Object> it=elements.iterator(); it.hasNext(); ) {
			Object item = it.next();
			if (item instanceof CustomAttribute)
				custom_attrs.add(_container.getTranslation(item));
			else
				standard_attrs.add(_container.getTranslation(item));
		}

		for (Iterator<String> it=standard_attrs.iterator(); it.hasNext(); )
			model.addElement(it.next());
		for (Iterator<String> it=custom_attrs.iterator(); it.hasNext(); )
			model.addElement(it.next());
	}
}


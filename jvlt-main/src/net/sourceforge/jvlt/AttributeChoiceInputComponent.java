package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class AttributeChoiceInputComponent extends ChoiceInputComponent {
	public AttributeChoiceInputComponent() {
		super(new IndentedComboBox());
		setTranslateItems(true);
	}
	
	protected void updateInputComponent() {
		Object[] choices = _container.getItems();
		TreeSet<AttributeChoice> root_set = new TreeSet<AttributeChoice>();
		ArrayList<AttributeChoice> roots = new ArrayList<AttributeChoice>();
		for (int i=0; i<choices.length; i++) {
			AttributeChoice choice = (AttributeChoice) choices[i];
			if (choice.getParent() == null && ! root_set.contains(choice)) {
				roots.add(choice);
				root_set.add(choice);
			}
		}
		
		for (Iterator<AttributeChoice> it=roots.iterator(); it.hasNext(); )
			insertChoice(it.next(), 0);
		
		if (choices.length > 0)
			_input_box.setSelectedIndex(0);
	}

	private void insertChoice(AttributeChoice choice, int indent_level) {
		((IndentedComboBox) _input_box).addItem(
			_container.getTranslation(choice), indent_level);
		AttributeChoice[] children = choice.getChildren();
		for (int i=0; i<children.length; i++)
			insertChoice(children[i], indent_level+1);
	}
}

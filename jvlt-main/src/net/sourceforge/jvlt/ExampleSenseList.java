package net.sourceforge.jvlt;

import javax.swing.*;

public class ExampleSenseList extends JList {
	private static final long serialVersionUID = 1L;
	
	private Example _example;
	private DefaultListModel _model;
	
	public ExampleSenseList(Example example) {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		_model = new DefaultListModel();
		setModel(_model);
		
		_example = example;
		update();
	}
	
	public Example.TextFragment getSelectedTextFragment() {
		int index = getSelectedIndex();
		if (index < 0)
			return null;
		
		return ((TextFragmentElement)_model.getElementAt(index)).getFragment();
	}
	
	public void update() {
		_model.clear();
		
		Example.TextFragment[] fragments = _example.getTextFragments();
		for (int i=0; i<fragments.length; i++) {
			Example.TextFragment fragment = fragments[i];
			if (fragment.getSense() == null)
				continue;
			
			TextFragmentElement elem = new TextFragmentElement(fragment);
			_model.addElement(elem);
		}
	}
}

class TextFragmentElement {
	private Example.TextFragment _fragment;
	
	public TextFragmentElement(Example.TextFragment fragment) {
		_fragment = fragment; }
	
	public Example.TextFragment getFragment() { return _fragment; }
	
	public String toString() {
		Sense s = _fragment.getSense();
		if (s == null)
			return "(null)";
		else
			return s.getParent().toString() + " - " + s.toString();
	}
}


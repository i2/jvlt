package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ExampleList extends JList {
	private static final long serialVersionUID = 1L;

	private Collection<Example> _examples;
	
	public ExampleList() {
		super(new DefaultListModel());
		_examples = new ArrayList<Example>();
	}
	
	public Collection<Example> getExamples() { return _examples; }
	
	public void setExamples(Collection<Example> examples) {
		_examples = examples;
		DefaultListModel model = (DefaultListModel) getModel();
		model.clear();
		for (Iterator<Example> it=examples.iterator(); it.hasNext(); )
			model.addElement(it.next().toString());
	}
}


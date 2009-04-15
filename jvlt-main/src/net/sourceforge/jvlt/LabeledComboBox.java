package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class LabeledComboBox extends JComboBox {
	private class DocumentEventHandler implements DocumentListener {
		public void insertUpdate(DocumentEvent ev) { fireChangeEvent(); }
	
		public void removeUpdate(DocumentEvent ev) { fireChangeEvent(); }
	
		public void changedUpdate(DocumentEvent ev) {}
	}
	
	private static final long serialVersionUID = 1L;
	
	private JLabel _label;
	private ArrayList<ChangeListener> _listeners;
	
	public LabeledComboBox() { this(new DefaultComboBoxModel()); }
	
	public LabeledComboBox(Object[] items) {
		super(items);
		
		init();
	}
	
	public LabeledComboBox(ComboBoxModel model) {
		super(model);
		
		init();
	}
	
	public void addChangeListener(ChangeListener l) { _listeners.add(l); }
	
	public void removeChangeListener(ChangeListener l) { _listeners.remove(l); }
	
	public void setLabel(String command) {
		super.setActionCommand(command);
		_label = GUIUtils.getLabel(command, this);
		_label.setEnabled(isEnabled());
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (_label != null)
			_label.setEnabled(enabled);
	}
	
	public JLabel getLabel() { return _label; }
	
	private void init() {
		_listeners = new ArrayList<ChangeListener>();
		
		JTextComponent tc = (JTextComponent) this.editor.getEditorComponent();
		tc.getDocument().addDocumentListener(new DocumentEventHandler());
	}

	private void fireChangeEvent() {
		for (Iterator<ChangeListener> it=_listeners.iterator(); it.hasNext(); )
			it.next().stateChanged(new ChangeEvent(this));
	}
}

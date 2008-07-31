package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.*;

import net.sourceforge.jvlt.event.CopyPastePopupListener;

class CustomTextField extends JTextField {
	private class DocumentEventHandler implements DocumentListener {
		public void insertUpdate(DocumentEvent ev) { fireChangeEvent(); }
	
		public void removeUpdate(DocumentEvent ev) { fireChangeEvent(); }
	
		public void changedUpdate(DocumentEvent ev) {}
	}
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ChangeListener> _change_listeners;
	private JLabel _label;
	
	public CustomTextField(int columns)	{
		super(columns);
		getDocument().addDocumentListener(new DocumentEventHandler());
		addMouseListener(new CopyPastePopupListener(this));
		_change_listeners = new ArrayList<ChangeListener>();
		_label = null;
	}
	
	public CustomTextField() { this(0); }
	
	public JLabel getLabel() { return _label; }
	
	public void setActionCommand(String command) {
		super.setActionCommand(command);
		_label = GUIUtils.getLabel(command, this);
	}
	
	public void addChangeListener(ChangeListener listener) {
		_change_listeners.add(listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		_change_listeners.remove(listener);
	}
	
	private void fireChangeEvent() {
		Iterator<ChangeListener> it = _change_listeners.iterator();
		while (it.hasNext())
			it.next().stateChanged(new ChangeEvent(this));
	}
}


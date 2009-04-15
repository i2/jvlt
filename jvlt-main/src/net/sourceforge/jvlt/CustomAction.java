package net.sourceforge.jvlt;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class CustomAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ActionListener> _listeners;
	private String _action_command;

	public CustomAction (String action_command) {
		_action_command = action_command;
		_listeners = new ArrayList<ActionListener>();
	}
	
	public void setActionCommand(String action_command) {
		_action_command = action_command;
	}
	
	public void addActionListener(ActionListener listener) {
		_listeners.add(listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		_listeners.remove(listener);
	}
	
	public void actionPerformed(ActionEvent e) {
		Iterator<ActionListener> it = _listeners.iterator();
		while (it.hasNext()) {
			it.next().actionPerformed(
				new ActionEvent(this, e.getID(), _action_command)); 
		}
	}
}

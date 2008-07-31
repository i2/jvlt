package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A panel that allows to manage lists of objects.
 */
public class ObjectListPanel extends JPanel {
	protected class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (ev.getActionCommand().equals("add")) {
				Object o = ObjectListPanel.this.toString(
					_input_component.getInput());
				if (o != null && ! _list_model.contains(o)) {
					_list_model.addElement(o);
					_list.setSelectedIndex(_list_model.size()-1);
				}
			} else if (ev.getActionCommand().equals("remove")) {
				int index = _list.getSelectedIndex();
				if (index>=0)
					_list_model.remove(index);
			}
		}
	}
	
	protected class ListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent ev) {
			if (ev.getValueIsAdjusting() == false)
				update();
		}
	}
	
	protected class ChangeHandler implements ChangeListener {
		public void stateChanged(ChangeEvent ev) { update(); }
	}
	
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<ListSelectionListener> _selection_listeners;
	protected ChangeHandler _change_handler = new ChangeHandler();
	
	protected Action _add_action;
	protected Action _remove_action;
	protected DefaultListModel _list_model;
	protected JList _list;
	protected ListeningInputComponent _input_component = null;
	
	public ObjectListPanel() { this(new StringInputComponent()); }

	public Object[] getSelectedObjects() { return _list_model.toArray(); }
	
	public void setSelectedObjects(Object[] objects) {
		Object[] vals = objects==null ? new Object[0] : objects;
		
		_list_model.clear();
		for (int i=0; i<vals.length; i++) {
			String s = toString(vals[i]);
			if (s != null)
				_list_model.addElement(s);
		}
		
		update();
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		_add_action.setEnabled(enabled);
		_remove_action.setEnabled(enabled);
		_input_component.getComponent().setEnabled(enabled);
		_list.setEnabled(enabled);
		if (enabled)
			update();
	}
	
	protected ObjectListPanel(ListeningInputComponent c) {
		_selection_listeners = new ArrayList<ListSelectionListener>();
		
		_input_component = c;
		_input_component.addChangeListener(_change_handler);

		init();
		update();
	}
	
	protected void init() {
		ActionHandler handler = new ActionHandler();
		_add_action = GUIUtils.createTextAction(handler, "add");
		_remove_action = GUIUtils.createTextAction(handler, "remove");

		_list_model = new DefaultListModel();
		_list = new JList(_list_model);
		_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_list.addListSelectionListener(new ListSelectionHandler());

		JScrollPane list_scrpane = new JScrollPane();
		list_scrpane.setPreferredSize(new Dimension(100, 100));
		list_scrpane.getViewport().setView(_list);

		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		add(_input_component.getComponent(), cc);
		cc.update(1, 0, 0.0, 0.0);
		add(new JButton(_add_action), cc);
		cc.update(0, 1, 1.0, 1.0, 1, 2);
		add(list_scrpane, cc);
		cc.update(1, 1, 0.0, 0.0, 1, 1);
		add(new JButton(_remove_action), cc);
		cc.update(1, 2, 0.0, 1.0, 1, 1);
		add(Box.createVerticalGlue(), cc);
	}
	
	protected void update() {
		String s = toString(_input_component.getInput());
		_add_action.setEnabled(s != null && ! _list_model.contains(s));
		_remove_action.setEnabled(_list.getSelectedIndex() >= 0);
	}
	
	protected String toString(Object o) {
		return o==null ? null : o.toString();
	}
}


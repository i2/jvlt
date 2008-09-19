package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.jvlt.event.ComponentReplacementListener;

public abstract class AbstractEntryDialog extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	
	protected class ComponentReplacementHandler
		implements ComponentReplacementListener {
		public void componentReplaced(ComponentReplacementEvent ev) {
			_text_field_panel.remove(ev.getOldComponent());
			CustomConstraints cc = new CustomConstraints();
			cc.update(0, 1, 1.0, 0.0, 2, 1);
			_text_field_panel.add(ev.getNewComponent(), cc);
			AbstractEntryDialog.this.pack();
		}
	}
	
	protected class ListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false)
				updateActions();
		}
	}
	
	protected class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("add")) {
				Sense sense = new Sense();
				SenseDialogData data = new SenseDialogData(
						_current_entry, sense);
				int result = CustomDialog.showDialog(data, getContentPane(),
					GUIUtils.getString("Labels", "add_sense"));
				if (result == CustomDialog.OK_OPTION) {
					AddDictObjectAction action = new AddDictObjectAction(sense);
					_sense_actions.add(action);
					Sense clone = (Sense) sense.clone();
					_list_model.addElement(clone);
					_sense_map.put(clone, sense);
				}
			} else if (e.getActionCommand().equals("edit")) {
				Object obj = _sense_list.getSelectedValue();
				if (obj == null)
					return;
				
				Sense sense = (Sense) obj;
				Sense clone = (Sense) sense.clone();
				SenseDialogData data = new SenseDialogData(
						_current_entry, clone);
				int result = CustomDialog.showDialog(data, getContentPane(),
					GUIUtils.getString("Labels", "edit_sense"));
				if (result == CustomDialog.OK_OPTION) {
					sense.reinit(clone);
					EditDictObjectAction action = new EditDictObjectAction(
						_sense_map.get(sense), clone);
					_sense_actions.add(action);
					_sense_list.revalidate();
					_sense_list.repaint(_sense_list.getVisibleRect());
				}
			} else if (e.getActionCommand().equals("remove"))	{
				int index = _sense_list.getSelectedIndex();
				if (index < 0)
					return;
					
				Sense sense = (Sense) _list_model.getElementAt(index);
				Sense orig_sense = _sense_map.get(sense);
				// If the parent of the sense is null, i.e. the sense has been
				// added in this dialog, there are no examples linked to it yet.
				if (orig_sense.getParent() != null) {
					Collection<Example> linked_examples =
						_model.getDict().getExamples(orig_sense);
					if (linked_examples.size() > 0) {
						MessageDialog.showDialog(getContentPane(),
							MessageDialog.WARNING_MESSAGE,
							GUIUtils.getString("Messages", "cannot_remove_sense"));
						return;
					}
				}
	
				int result = JOptionPane.showConfirmDialog(getContentPane(),
					GUIUtils.getString("Messages", "remove_sense"),
					GUIUtils.getString("Labels", "confirm"),
					JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					RemoveSenseAction action = new RemoveSenseAction(
						_sense_map.get(sense), index);
					_sense_actions.add(action);
					_list_model.remove(index);
					_sense_map.remove(orig_sense);
				}
			} else if(e.getActionCommand().equals("up")) {
				moveSelectedElement(-1);
			} else if(e.getActionCommand().equals("down")) {
				moveSelectedElement(1);
			} else if (e.getActionCommand().equals("advanced")) {
				CustomDialog.showDialog(getAdvancedDialogData(),
					getContentPane(), GUIUtils.getString("Labels", "advanced"));
			}
		}
	}
	
	protected JVLTModel _model = null;
	protected HashMap<Sense, Sense> _sense_map = null;
	protected ArrayList<DictObjectAction> _sense_actions = null;
	protected Entry _current_entry = null;
	
	protected CustomTextField _orth_field;
	protected StringListEditor _pronunciation_editor;
	protected JList _sense_list;
	protected DefaultListModel _list_model;
	protected CustomAction _add_action;
	protected CustomAction _advanced_action;
	protected CustomAction _edit_action;
	protected CustomAction _remove_action;
	protected CustomAction _sense_up_action;
	protected CustomAction _sense_down_action;

	protected JPanel _text_field_panel;
	protected JComponent _pronunciation_component;

	public AbstractEntryDialog(Frame owner, String title, JVLTModel model) {
		super(owner, title, true);
		_model = model;

		_sense_map = new HashMap<Sense, Sense>();
		_sense_actions = new ArrayList<DictObjectAction>();

		init();
		updateActions();
	}
	
	protected void setCurrentEntry(Entry entry) {
		_current_entry = entry;
		_sense_map.clear();
		_sense_actions.clear();
		_list_model.clear();
		
		if (entry != null)
		{
			Sense[] senses = _current_entry.getSenses();
			for (int i=0; i<senses.length; i++) {
				Sense clone = (Sense) senses[i].clone();
				_sense_map.put(clone, senses[i]);
				_list_model.addElement(clone);
			}
		}

		updateComponents();
		updateActions();
	}
	
	protected void updateComponents() {
		_orth_field.setEnabled(false);
		_pronunciation_editor.setEnabled(false);
		_sense_list.setEnabled(false);

		if (_current_entry != null) {
			_orth_field.setEnabled(true);
			_pronunciation_editor.setEnabled(true);
			_sense_list.setEnabled(true);

			_orth_field.setText(_current_entry.getOrthography());
			_pronunciation_editor.setSelectedItems(
				_current_entry.getPronunciations());
		} else {
			_orth_field.setText("");
			_pronunciation_editor.setSelectedItems(new String[0]);
		}
	}

	protected void updateActions() {
		_add_action.setEnabled(false);
		_edit_action.setEnabled(false);
		_remove_action.setEnabled(false);
		_sense_up_action.setEnabled(false);
		_sense_down_action.setEnabled(false);
		
		if (_current_entry == null)
			return;

		int selected_sense = _sense_list.getSelectedIndex();
		_add_action.setEnabled(true);
		_edit_action.setEnabled(selected_sense >= 0);
		_remove_action.setEnabled(selected_sense >= 0);
		_sense_up_action.setEnabled(selected_sense > 0);
		_sense_down_action.setEnabled(selected_sense >= 0 &&
				selected_sense != _list_model.getSize()-1);
	}

	protected void updateCurrentEntry()
			throws InvalidDataException {
		if (_current_entry == null)
			return;
		
		if (_list_model.getSize() == 0)
			throw new InvalidDataException(GUIUtils.getString(
				"Messages", "no_sense"));

		if (_orth_field.getText().equals(""))
			throw new InvalidDataException(GUIUtils.getString(
				"Messages", "empty_orthography"));

		_current_entry.setOrthography(_orth_field.getText());
		_current_entry.setPronunciations(Utils.objectArrayToStringArray(
			_pronunciation_editor.getSelectedItems()));
		Entry e = _model.getDict().getEntry(_current_entry);
		if (e != null && ! e.getID().equals(_current_entry.getID()))
			throw new InvalidDataException(GUIUtils.getString(
				"Messages", "duplicate_entry"));
	}
	
	protected abstract AdvancedEntryDialogData getAdvancedDialogData();
	
	private void init() {
		ActionHandler handler = new ActionHandler();
		
		_orth_field  = new CustomTextField (20);
		_orth_field.setActionCommand("orthography");

		_pronunciation_editor  = new StringListEditor("pronunciation");
		_pronunciation_editor.addComponentReplacementListener(
			new ComponentReplacementHandler());
		_pronunciation_component = _pronunciation_editor.getInputComponent();
		
		_list_model = new DefaultListModel();
		_sense_list = new JList(_list_model);
		_sense_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_sense_list.addListSelectionListener(new ListSelectionHandler());
		JScrollPane scrpane = new JScrollPane();
		scrpane.setPreferredSize(new Dimension(300, 100));
		scrpane.getViewport().setView(_sense_list);
		
		_add_action = GUIUtils.createTextAction(handler, "add");
		_advanced_action = GUIUtils.createTextAction(handler, "advanced");
		_edit_action = GUIUtils.createTextAction(handler, "edit");
		_remove_action = GUIUtils.createTextAction(handler, "remove");
		_sense_up_action = GUIUtils.createIconAction(handler, "up");
		_sense_down_action = GUIUtils.createIconAction(handler, "down");
		
		JButton add_button = new JButton(_add_action);
		JButton advanced_button = new JButton(_advanced_action);
		JButton edit_button = new JButton(_edit_action);
		JButton remove_button = new JButton(_remove_action);
		
		_text_field_panel = new JPanel();
		_text_field_panel.setLayout (new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 0.0, 0.0);
		_text_field_panel.add(_orth_field.getLabel(), cc);
		cc.update(1, 0, 1.0, 0.0);
		_text_field_panel.add(_orth_field, cc);
		cc.update(0, 1, 1.0, 0.0, 2, 1);
		_text_field_panel.add(_pronunciation_component, cc);
		
		ButtonPanel button_panel = new ButtonPanel(
			SwingConstants.VERTICAL, SwingConstants.TOP);
		button_panel.addButton(add_button);
		button_panel.addButton(edit_button);
		button_panel.addButton(remove_button);
		
		ButtonPanel advanced_button_panel = new ButtonPanel(
			SwingConstants.HORIZONTAL, SwingConstants.RIGHT);
		advanced_button_panel.addButton(advanced_button);
		
		JPanel sense_panel = new JPanel();
		sense_panel.setLayout (new GridBagLayout());
		cc.reset();
		cc.update(0, 0);
		sense_panel.add(new JButton(_sense_up_action), cc);
		cc.update(0, 1);
		sense_panel.add(new JButton(_sense_down_action), cc);
		cc.update(0, 2, 0.0, 1.0, 1, GridBagConstraints.REMAINDER);
		sense_panel.add(Box.createVerticalGlue(), cc);
		cc.update(1, 0, 1.0, 1.0);
		sense_panel.add(scrpane, cc);
		cc.update(2, 0, 0.0, 1.0);
		sense_panel.add(button_panel, cc);
		sense_panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			GUIUtils.getString("Labels", "senses")));
		
		Container content_pane = new JPanel();
		content_pane.setLayout (new GridBagLayout());
		cc.reset();
		cc.update(0, 0, 1.0, 0.0);
		content_pane.add (_text_field_panel, cc);
		cc.update(0, 1, 1.0, 1.0);
		content_pane.add (sense_panel, cc);
		cc.update(0, 2, 1.0, 0.0);
		content_pane.add (advanced_button_panel, cc);
		setContent(content_pane);
	}
	
	private void moveSelectedElement(int direction)	{
		int index = _sense_list.getSelectedIndex();
		int new_index = index+direction;
		Object obj = _list_model.remove(index);
		_list_model.add(new_index, obj);
		_sense_list.setSelectedIndex(new_index);
		
		Sense sense = (Sense) obj;
		MoveSenseAction action = new MoveSenseAction(
			_sense_map.get(sense), index, new_index);
		_sense_actions.add(action);
	}
}

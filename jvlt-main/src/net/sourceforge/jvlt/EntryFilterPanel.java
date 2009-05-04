package net.sourceforge.jvlt;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sourceforge.jvlt.event.DialogListener;

public class EntryFilterPanel extends JPanel {
	public static final int MODE_MULTI = 0;
	public static final int MODE_ORIGINAL = 1;
	public static final int MODE_PRONUNCIATION = 2;
	public static final int MODE_TRANSLATION = 3;
	public static final int MODE_DEFINITION = 4;
	public static final int MODE_CATEGORY = 5;
	public static final int MODE_LESSON = 6;
	public static final int MODE_ADVANCED = 2;
	
	private static final long serialVersionUID = 1L;
	
	private static class BasicEntryFilter extends EntryFilter {
		private ObjectQueryItem _item = null;
		
		public BasicEntryFilter(ObjectQueryItem item) {
			_item = item;
			if (item instanceof StringQueryItem)
				((StringQueryItem) item).setMatchCase(false);
			else if (item instanceof ObjectArrayQueryItem)
				((ObjectArrayQueryItem) item).setMatchCase(false);
			else if (item instanceof SenseArrayQueryItem)
				((SenseArrayQueryItem) item).setMatchCase(false);
			
			_query = new ObjectQuery(Entry.class);
			_query.setType(ObjectQuery.MATCH_ONE);
			_query.addItem(item);
		}
		
		public String getFilterString() { return (String) _item.getValue(); }
		
		public void setFilterString(String str) { _item.setValue(str); }
	}
	
	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == _advanced_button) {
				if (_query_dialog.isVisible()) {
					_query_dialog.setVisible(false);
				} else {
					Frame f =
						JOptionPane.getFrameForComponent(EntryFilterPanel.this);
					GUIUtils.showDialog(f, _query_dialog);
				}
			} else if (e.getSource() == _filter_field) {
				setFilterString(_filter_field.getText());
				
				fireActionEvent(new ActionEvent(EntryFilterPanel.this,
						e.getID(), null));
			} else if (e.getSource() == _mode_box) {
				Object selected = _mode_box.getSelectedItem();
				if (selected.equals(GUIUtils.getString("Labels", "filter_all")))
					setMode(MODE_MULTI);
				else if (selected.equals(
						GUIUtils.getString("Labels", "original")))
					setMode(MODE_ORIGINAL);
				else if (selected.equals(
						GUIUtils.getString("Labels", "pronunciation")))
					setMode(MODE_PRONUNCIATION);
				else if (selected.equals(
						GUIUtils.getString("Labels", "translation")))
					setMode(MODE_TRANSLATION);
				else if (selected.equals(
						GUIUtils.getString("Labels", "definition")))
					setMode(MODE_DEFINITION);
				else if (selected.equals(
						GUIUtils.getString("Labels", "category")))
					setMode(MODE_CATEGORY);
				else if (selected.equals(
						GUIUtils.getString("Labels", "lesson")))
					setMode(MODE_LESSON);
				else if (selected.equals(
						GUIUtils.getString("Labels", "filter_advanced")))
					setMode(MODE_ADVANCED);
			} else if (e.getActionCommand().equals("ok")) {
				if (_mode != MODE_ADVANCED)
					setFilterString(_filter_field.getText());
				
				fireActionEvent(new ActionEvent(EntryFilterPanel.this,
						e.getID(), null));
			} else if (e.getActionCommand().equals("cancel")) {
				if (_mode == MODE_ADVANCED) {
					ObjectQuery empty_query = new ObjectQuery();
					_query_dialog.setObjectQuery(empty_query);
					_filters.get(MODE_ADVANCED).setQuery(empty_query);
					updateFilterField();
				} else {
					setFilterString("");
				}
				
				fireActionEvent(new ActionEvent(EntryFilterPanel.this,
						e.getID(), null));
			}
		}
	}
	
	private class DialogHandler implements DialogListener {
		public void dialogStateChanged(DialogEvent e) {
			if (e.getSource() == _query_dialog) {
				if (e.getType() == AbstractDialog.APPLY_OPTION) {
					ObjectQuery oq = _query_dialog.getObjectQuery();
					_filters.get(MODE_ADVANCED).setQuery(oq);
					updateFilterField();
					fireActionEvent(new ActionEvent(EntryFilterPanel.this,
							ActionEvent.ACTION_PERFORMED, null));
				} else if (e.getType() == AbstractDialog.CLOSE_OPTION) {
					_query_dialog.setVisible(false);
				}
			}
		}
	}
	
	private int _mode;
	private JVLTModel _model;
	private Map<Integer, EntryFilter> _filters;
	private Set<ActionListener> _listeners;
	
	private CustomTextField _filter_field;
	private EntryQueryDialog _query_dialog;
	private JButton _advanced_button;
	private JButton _ok_button;
	private JButton _cancel_button;
	private JComboBox _mode_box;
	
	public EntryFilterPanel(JVLTModel model) {
		_mode = MODE_MULTI;
		_model = model;
		_listeners = new HashSet<ActionListener>();
		
		//
		// Create entry filters
		//
		_filters = new HashMap<Integer, EntryFilter>();
		EntryFilter filter = new SimpleEntryFilter();
		((SimpleEntryFilter) filter).setMatchCase(false);
		_filters.put(MODE_MULTI, filter);
		_filters.put(MODE_ORIGINAL, new BasicEntryFilter(new StringQueryItem(
				"Orthography", StringQueryItem.CONTAINS, "")));
		_filters.put(MODE_PRONUNCIATION, new BasicEntryFilter(
				new ObjectArrayQueryItem("Pronunciations",
						ObjectArrayQueryItem.ITEM_CONTAINS, "")));
		_filters.put(MODE_TRANSLATION, new BasicEntryFilter(
				new SenseArrayQueryItem(
						SenseArrayQueryItem.TRANSLATION_CONTAINS, "")));
		_filters.put(MODE_DEFINITION, new BasicEntryFilter(
				new SenseArrayQueryItem(
						SenseArrayQueryItem.DEFINITION_CONTAINS, "")));
		_filters.put(MODE_CATEGORY, new BasicEntryFilter(
				new ObjectArrayQueryItem("Categories",
						ObjectArrayQueryItem.ITEM_CONTAINS, "")));
		_filters.put(MODE_LESSON, new BasicEntryFilter(new StringQueryItem(
				"Lesson", StringQueryItem.CONTAINS, "")));
		_filters.put(MODE_ADVANCED, new EntryFilter());
		
		//
		// Create UI
		//
		_query_dialog = new EntryQueryDialog(
				JOptionPane.getFrameForComponent(this),
				GUIUtils.getString("Labels", "advanced_filter"), _model);
		_query_dialog.addDialogListener(new DialogHandler());
		
		ActionHandler handler = new ActionHandler();
		
		_filter_field = new CustomTextField();
		_filter_field.setActionCommand("filter");
		_filter_field.addActionListener(handler);
		
		_advanced_button = new JButton("...");
		_advanced_button.addActionListener(handler);
		
		_ok_button = new JButton(GUIUtils.createIconAction(handler, "ok"));
		
		_cancel_button = new JButton(
				GUIUtils.createIconAction(handler, "cancel"));
		
		_mode_box = new JComboBox();
		_mode_box.addActionListener(handler);
		_mode_box.addItem(GUIUtils.getString("Labels", "filter_all"));
		_mode_box.addItem(GUIUtils.getString("Labels", "original"));
		_mode_box.addItem(GUIUtils.getString("Labels", "pronunciation"));
		_mode_box.addItem(GUIUtils.getString("Labels", "translation"));
		_mode_box.addItem(GUIUtils.getString("Labels", "definition"));
		_mode_box.addItem(GUIUtils.getString("Labels", "category"));
		_mode_box.addItem(GUIUtils.getString("Labels", "lesson"));
		_mode_box.addItem(GUIUtils.getString("Labels", "filter_advanced"));
		
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 0.0, 0.0);
		add(_filter_field.getLabel(), cc);
		cc.update(1, 0, 1.0, 0.0);
		add(_filter_field, cc);
		cc.update(2, 0, 0.0, 0.0);
		add(_cancel_button, cc);
		cc.update(3, 0, 0.0, 0.0);
		add(_ok_button, cc);
		cc.update(4, 0, 0.0, 0.0);
		add(_mode_box, cc);
	}
	
	public int getMode() { return _mode; }
	
	public void setMode(int mode) {
		if (mode == _mode)
			return;
		
		_mode = mode;

		CustomConstraints cc = new CustomConstraints();
		cc.update(3, 0, 0.0, 0.0);
		if (mode == MODE_ADVANCED) {
			// Update filter field text
			updateFilterField();

			// Remove ok button, add button for advanced dialog
			remove(_ok_button);
			add(_advanced_button, cc);
			revalidate();
		} else {
			// Update filter field text
			updateFilterField();
			
			// Remove button for advanced dialog, add ok button
			remove(_advanced_button);
			add(_ok_button, cc);
			revalidate();
		}
	}
	
	public EntryFilter getFilter() { return _filters.get(_mode); }
	
	public String getFilterString() {
		EntryFilter filter = _filters.get(_mode);
		if (filter instanceof SimpleEntryFilter) {
			return ((SimpleEntryFilter) filter).getFilterString();
		} else if (filter instanceof BasicEntryFilter) {
			return ((BasicEntryFilter) filter).getFilterString();
		} else {
			return null;
		}
	}
	
	public void setFilterString(String str) {
		EntryFilter filter = _filters.get(_mode);
		if (filter instanceof SimpleEntryFilter) {
			((SimpleEntryFilter) filter).setFilterString(str);
		} else if (filter instanceof BasicEntryFilter) {
			((BasicEntryFilter) filter).setFilterString(str);
		}
		
		updateFilterField();
	}
	
	public void addActionListener(ActionListener l) { _listeners.add(l); }
	
	private void fireActionEvent(ActionEvent e) {
		for (Iterator<ActionListener> i = _listeners.iterator(); i.hasNext(); )
			i.next().actionPerformed(
					new ActionEvent(this, e.getID(), e.getActionCommand()));
	}
	
	private void updateFilterField() {
		if (_mode == MODE_ADVANCED) {
			_filter_field.setEnabled(false);
			ObjectQuery oq = _filters.get(_mode).getQuery();
			if (oq.getName() == null || oq.getName().equals(""))
				_filter_field.setText(
						GUIUtils.getString("Labels", "filter_unnamed"));
			else
				_filter_field.setText(oq.getName());
		} else {
			_filter_field.setEnabled(true);
			_filter_field.setText(getFilterString());
		}
	}
}

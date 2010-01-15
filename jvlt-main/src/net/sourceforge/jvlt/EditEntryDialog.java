package net.sourceforge.jvlt;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jvlt.event.DialogListener;

public class EditEntryDialog extends AbstractEntryDialog {
	private static final long serialVersionUID = 1L;
	
	private class DialogHandler implements DialogListener {
		public void dialogStateChanged(DialogEvent ev) {
			if (ev.getType() == OK_OPTION) {
				try {
					updateEntries();
					
					// Create action and execute it
					ArrayList<EditEntryAction> actions =
						new ArrayList<EditEntryAction>();
					for (int i=0; i<_entries.size(); i++) {
						EditEntryAction eea = new EditEntryAction(
							_orig_entries.get(i), _entries.get(i));

						// The senses can be modified only when editing
						// a single entry
						if (_mode == MODE_EDIT_ENTRY)
							eea.addSenseActions(_sense_actions.toArray(
									new DictObjectAction[0]));

						actions.add(eea);
					}
					EditEntriesAction action = new EditEntriesAction(
						actions.toArray(new EditEntryAction[0]));
					action.setMessage(
							GUIUtils.getString("Actions", "edit_entries",
							new Object[] { actions.size() }));
					_model.getDictModel().executeAction(action);
					
					setVisible(false);
				}
				catch (InvalidDataException e) {
					MessageDialog.showDialog(getContentPane(),
							MessageDialog.WARNING_MESSAGE, e.getMessage());
				}
			}
			else if (ev.getType() == CANCEL_OPTION)
				setVisible(false);
		}
	}

	private static final int MODE_INVALID = 0;
	private static final int MODE_EDIT_ENTRY = 1;
	private static final int MODE_EDIT_ENTRIES = 2;
	
	private int _mode = MODE_INVALID;
	private String _orig_lesson = null;
	
	private List<Entry> _orig_entries = new ArrayList<Entry>();
	private List<Entry> _entries = new ArrayList<Entry>();
	
	public EditEntryDialog(Frame owner, String title, JVLTModel model) {
		super(owner, title, model);
		
		setButtons(new int[] { OK_OPTION, CANCEL_OPTION });
		addDialogListener(new DialogHandler());
	}
	
	public void init(List<Entry> entries) {
		super.init();
		
		_orig_entries = entries;
		_entries.clear();
		for (Iterator<Entry> it=_orig_entries.iterator(); it.hasNext(); )
			_entries.add((Entry) it.next().clone());

		if (entries.size() == 0)
			_mode = MODE_INVALID;
		else if (entries.size() == 1)
			_mode = MODE_EDIT_ENTRY;
		else
			_mode = MODE_EDIT_ENTRIES;
		
		if (_entries.size() == 1)
			setCurrentEntry(_entries.get(0));
		else
			setCurrentEntry(null);
	}
	
	protected void updateComponents() {
		super.updateComponents();

		if (_current_entry != null) {
			// Only one entry is edited
			_orig_lesson = _current_entry.getLesson();
			_lesson_box.setEnabled(true);
			_lesson_box.setSelectedItem(_current_entry.getLesson());
		} else {
			//
			// Multiple entries are edited. The lesson list can only be edited
			// if it is the same for all entries.
			// 
			boolean enabled = true;
			_orig_lesson = _entries.get(0).getLesson();
			for (int i=1; i<_entries.size(); i++)
				if (! _orig_lesson.equals(_entries.get(i).getLesson())) {
					_orig_lesson = "";
					enabled = false;
					break;
				}
			
			_lesson_box.setSelectedItem(_orig_lesson);
			_lesson_box.setEnabled(enabled);
		}
	}
	
	protected void updateEntries() throws InvalidDataException {
		super.updateEntries();
		
		String lesson = _lesson_box.getSelectedItem().toString();
		if (! lesson.equals(_orig_lesson))
			for (Iterator<Entry> it=_entries.iterator(); it.hasNext(); )
				it.next().setLesson(lesson);
	}

	protected AdvancedEntryDialogData getAdvancedDialogData() {
		return new AdvancedEntryDialogData(_entries, _model);
	}
}

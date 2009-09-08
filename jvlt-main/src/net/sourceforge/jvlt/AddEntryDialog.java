package net.sourceforge.jvlt;

import java.awt.Frame;
import java.util.ArrayList;

import net.sourceforge.jvlt.event.DialogListener;

public class AddEntryDialog extends AbstractEntryDialog {
	private static final long serialVersionUID = 1L;
	
	private static final int APPLY_AND_EDIT_OPTION = USER_OPTION;
	
	private class DialogHandler implements DialogListener {
		public void dialogStateChanged(DialogEvent ev) {
			try {
				if (ev.getType() == APPLY_OPTION) {
					AddEntryDialog.this.apply();
					AddEntryDialog.this.init();
				} else if (ev.getType() == APPLY_AND_EDIT_OPTION) {
					AddEntryDialog.this.apply();
					AddEntryDialog.this.init(_current_entry.createDeepCopy());
			    } else if (ev.getType() == CLOSE_OPTION) {
					setVisible(false);
				}
			} catch (InvalidDataException e) {
				MessageDialog.showDialog(getContentPane(),
						MessageDialog.WARNING_MESSAGE, e.getMessage());
			}
		}
	}
	
	public AddEntryDialog(Frame owner, String title, JVLTModel model) {
		super(owner, title, model);
		
		setButtons(new int[] {
				APPLY_OPTION, APPLY_AND_EDIT_OPTION, CLOSE_OPTION });
		addDialogListener(new DialogHandler());
	}
	
	public void init() {
		init(new Entry(_model.getDict().getNextUnusedEntryID()));
	}
	
	public void init(Entry entry) {
		super.init();
		
		// Initialize with empty entry. Use the lesson of the entry last added
		if (_current_entry != null)
			entry.setLesson(_current_entry.getLesson());
			
		setCurrentEntry(entry);
	}
	
	protected void updateComponents() {
		super.updateComponents();
		
		_lesson_box.setEnabled(false);
		if (_current_entry != null) {
			_lesson_box.setEnabled(true);
			_lesson_box.setSelectedItem(_current_entry.getLesson());
		} else {
			_lesson_box.setSelectedItem("");
		}
	}

	protected AdvancedEntryDialogData getAdvancedDialogData() {
		ArrayList<Entry> entries = new ArrayList<Entry>();
		entries.add(_current_entry);
		return new AdvancedEntryDialogData(entries, _model);
	}
	
	@Override
	protected String getFieldNameForCustomValue(int value) {
		if (value == APPLY_AND_EDIT_OPTION)
			return "APPLY_AND_EDIT_OPTION";
		else
			return super.getFieldNameForCustomValue(value);
	}
	
	@Override
	protected int getValueForCustomFieldName(String name) {
		if (name.equals("APPLY_AND_EDIT_OPTION"))
			return APPLY_AND_EDIT_OPTION;
		else
			return super.getValueForCustomFieldName(name);
	}
	
	private void apply() throws InvalidDataException {
		updateEntries();
		
		// Apply sense actions
		EditEntryAction eea = new EditEntryAction(
			_current_entry, _current_entry);
		eea.addSenseActions(_sense_actions.toArray(new DictObjectAction[0]));
		try { eea.executeAction(); }
		catch (DictException ex) {} // Should not happen

		// Create and execute action
		AddDictObjectAction action = new AddDictObjectAction(_current_entry);
		action.setMessage(GUIUtils.getString("Actions", "add_entry"));
		_model.getDictModel().executeAction(action);
	}
}

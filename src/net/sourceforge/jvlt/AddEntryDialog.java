package net.sourceforge.jvlt;

import java.awt.Frame;
import java.util.ArrayList;

import net.sourceforge.jvlt.event.DialogListener;

public class AddEntryDialog extends AbstractEntryDialog {
	private static final long serialVersionUID = 1L;
	
	private class DialogHandler implements DialogListener {
		public void dialogStateChanged(DialogEvent ev) {
			if (ev.getType() == APPLY_OPTION) {
				try {
					updateEntries();
					
					// Apply sense actions
					EditEntryAction eea = new EditEntryAction(
						_current_entry, _current_entry);
					eea.addSenseActions(_sense_actions.toArray(
							new DictObjectAction[0]));
					try { eea.executeAction(); }
					catch (DictException ex) {} // Should not happen

					// Create and execute action
					AddDictObjectAction action =
							new AddDictObjectAction(_current_entry);
					action.setMessage(
							GUIUtils.getString("Actions", "add_entry"));
					_model.getDictModel().executeAction(action);
					
					AddEntryDialog.this.init();
				}
				catch (InvalidDataException e) {
					MessageDialog.showDialog(getContentPane(),
							MessageDialog.WARNING_MESSAGE, e.getMessage());
				}
			}
			else if (ev.getType() == CLOSE_OPTION)
				setVisible(false);
		}
	}
	
	public AddEntryDialog(Frame owner, String title, JVLTModel model) {
		super(owner, title, model);
		
		setButtons(new int[] { APPLY_OPTION, CLOSE_OPTION });
		addDialogListener(new DialogHandler());
	}
	
	public void init() {
		super.init();
		
		// Initialize with empty entry
		setCurrentEntry(new Entry(_model.getDict().getNextUnusedEntryID()));
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
}

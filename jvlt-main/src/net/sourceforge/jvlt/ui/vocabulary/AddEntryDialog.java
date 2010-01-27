package net.sourceforge.jvlt.ui.vocabulary;

import java.awt.Frame;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import net.sourceforge.jvlt.actions.AddDictObjectAction;
import net.sourceforge.jvlt.actions.DictObjectAction;
import net.sourceforge.jvlt.actions.EditEntryAction;
import net.sourceforge.jvlt.core.DictException;
import net.sourceforge.jvlt.core.Entry;
import net.sourceforge.jvlt.event.DialogListener;
import net.sourceforge.jvlt.model.JVLTModel;
import net.sourceforge.jvlt.ui.dialogs.InvalidDataException;
import net.sourceforge.jvlt.ui.dialogs.MessageDialog;
import net.sourceforge.jvlt.ui.utils.GUIUtils;

public class AddEntryDialog extends AbstractEntryDialog {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(AddEntryDialog.class);

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

		setButtons(new int[] { APPLY_OPTION, APPLY_AND_EDIT_OPTION,
				CLOSE_OPTION });
		addDialogListener(new DialogHandler());
	}

	@Override
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

	@Override
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

	@Override
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
		EditEntryAction eea = new EditEntryAction(_current_entry,
				_current_entry);
		eea.addSenseActions(_sense_actions.toArray(new DictObjectAction[0]));
		try {
			eea.executeAction();
		} catch (DictException ex) {
			// TODO write a message about what happened
			logger.error(ex);
		}

		// Create and execute action
		AddDictObjectAction action = new AddDictObjectAction(_current_entry);
		action.setMessage(GUIUtils.getString("Actions", "add_entry"));
		_model.getDictModel().executeAction(action);
	}
}

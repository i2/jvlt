package net.sourceforge.jvlt.ui.vocabulary.entrydialog;

import net.sourceforge.jvlt.core.Entry;
import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.model.JVLTModel;
import net.sourceforge.jvlt.ui.dialogs.InvalidDataException;
import net.sourceforge.jvlt.ui.utils.GUIUtils;

public class AddSenseDialogData extends SenseDialogData {
	protected Entry _entry;
	
	public AddSenseDialogData(JVLTModel model, Entry entry) {
		super(model, new Sense());
		
		_entry = entry;
	}
	
	@Override
	protected void check(Sense sense) throws InvalidDataException {
		if (_entry.getSense(sense) != null) {
			throw new InvalidDataException(GUIUtils.getString("Messages",
					"duplicate_sense"));
		}
	}
}
